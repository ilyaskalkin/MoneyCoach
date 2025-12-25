package ru.iskalkin.moneycoach.service;

import ru.iskalkin.moneycoach.MoneyCoachApplication;
import ru.iskalkin.moneycoach.dto.OperationSearchRequest;
import ru.iskalkin.moneycoach.model.Operation;
import ru.iskalkin.moneycoach.repository.OperationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MoneyCoachApplication.class)
@ActiveProfiles("test")
class OperationServiceTest {

    @Autowired
    private OperationService service;

    @Autowired
    private OperationRepository repository;

    @BeforeEach
    void setUp() {
        // Просто убеждаемся, что база не пустая (3 записи из data.sql минимум)
        List<Operation> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void testGetAll() {
        List<Operation> all = service.getAll();
        // Должно быть минимум 3 записи из data.sql
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void testAdd() {
        int before = repository.findAll().size();

        Operation op = Operation.builder()
                .date(LocalDate.of(2025, 2, 1))
                .amount(new BigDecimal("999.99"))
                .description("Bonus")
                .kind((short) 1)
                .account((short) 1)
                .storned(false)
                .build();

        Operation saved = service.add(op);

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findAll()).hasSize(before + 1);
    }

    @Test
    void testDelete() {
        // Берём любую существующую операцию
        Operation existing = repository.findAll().get(0);
        Long id = existing.getId();

        service.delete(id);

        assertThat(repository.existsById(id)).isFalse();
    }

    @Test
    void testFindByDateRange() {
        OperationSearchRequest req = new OperationSearchRequest();
        req.setFromDate(LocalDate.of(2025, 1, 11));
        req.setToDate(LocalDate.of(2025, 1, 20));

        List<Operation> result = service.find(req);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(o ->
                !o.getDate().isBefore(req.getFromDate())
                        && !o.getDate().isAfter(req.getToDate()));
    }

    @Test
    void testFindByKindAndAccountAndDescription() {
        OperationSearchRequest req = new OperationSearchRequest();
        req.setKind((short) 2);
        req.setAccount((short) 1);
        req.setDescription("gro"); // попадает в "Groceries" из data.sql

        List<Operation> result = service.find(req);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(o ->
                o.getKind() == 2
                        && o.getAccount() == 1
                        && o.getDescription().toLowerCase().contains("gro"));
    }
}
