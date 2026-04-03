package ru.iskalkin.moneycoach.telegram;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ScreenshotBot extends TelegramLongPollingBot {

    private static final DateTimeFormatter FILE_NAME_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.allowed-chat-id}")
    private long allowedChatId;

    @Value("${telegram.bot.screenshots-dir}")
    private String screenshotsDir;

    private final Set<String> knownHashes = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void init() throws TelegramApiException {
        Path dir = Paths.get(screenshotsDir);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot create screenshots dir: " + dir, e);
            }
        }
        indexExistingFiles(dir);

        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(this);
        log.info("ScreenshotBot started, saving to {}", dir.toAbsolutePath());
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return "ScreenshotBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;

        long chatId = update.getMessage().getChatId();
        if (chatId != allowedChatId) {
            reply(chatId, "Доступ запрещён.");
            return;
        }

        if (!update.getMessage().hasPhoto()) {
            reply(chatId, "Пришли картинку.");
            return;
        }

        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize largest = photos.stream()
                .max(Comparator.comparingInt(PhotoSize::getFileSize))
                .orElseThrow();

        try {
            byte[] data = downloadPhoto(largest.getFileId());
            String hash = sha256(data);

            if (knownHashes.contains(hash)) {
                reply(chatId, "Такой скриншот уже есть (совпадает хеш SHA-256).");
                return;
            }

            String filename = "screenshot_" + LocalDateTime.now().format(FILE_NAME_FMT) + ".jpg";
            Path target = Paths.get(screenshotsDir, filename);
            Files.write(target, data);
            knownHashes.add(hash);

            reply(chatId, "Сохранено: " + filename);
            log.info("Saved screenshot {} ({} bytes)", filename, data.length);

        } catch (Exception e) {
            log.error("Failed to save screenshot", e);
            reply(chatId, "Ошибка при сохранении: " + e.getMessage());
        }
    }

    private byte[] downloadPhoto(String fileId) throws TelegramApiException, IOException {
        GetFile getFile = new GetFile(fileId);
        org.telegram.telegrambots.meta.api.objects.File tgFile = execute(getFile);
        String url = "https://api.telegram.org/file/bot" + botToken + "/" + tgFile.getFilePath();
        try (InputStream in = URI.create(url).toURL().openStream()) {
            return in.readAllBytes();
        }
    }

    private void reply(long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
        } catch (TelegramApiException e) {
            log.warn("Could not send message to {}: {}", chatId, e.getMessage());
        }
    }

    private void indexExistingFiles(Path dir) {
        try (var stream = Files.list(dir)) {
            stream.filter(Files::isRegularFile).forEach(file -> {
                try {
                    knownHashes.add(sha256(Files.readAllBytes(file)));
                } catch (Exception e) {
                    log.warn("Cannot hash existing file {}: {}", file, e.getMessage());
                }
            });
            log.info("Indexed {} existing screenshot(s)", knownHashes.size());
        } catch (IOException e) {
            log.warn("Cannot scan screenshots dir: {}", e.getMessage());
        }
    }

    private static String sha256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(md.digest(data));
    }
}
