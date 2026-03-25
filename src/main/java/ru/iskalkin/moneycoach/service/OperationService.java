package ru.iskalkin.moneycoach.service;

import lombok.extern.slf4j.Slf4j;
import ru.iskalkin.moneycoach.dto.OperationDto;
import ru.iskalkin.moneycoach.dto.OperationSearchRequest;
import ru.iskalkin.moneycoach.model.Operation;
import ru.iskalkin.moneycoach.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OperationService {

    private final OperationRepository repository;

    public OperationDto add(OperationDto dto) {
        log.info("calling add: " + dto);
        Operation operation = dto.toEntity();
        operation.setId(null);
        return OperationDto.from(repository.save(operation));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<OperationDto> getAll() {
        log.info("calling getAll");
        return repository.findAll().stream().map(OperationDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<OperationDto> find(OperationSearchRequest request) {
        // Примитивный фильтр по критериям
        List<Operation> all = repository.findAll();

        return all.stream()
                .filter(o -> request.getFromDate() == null
                        || !o.getDate().isBefore(request.getFromDate()))
                .filter(o -> request.getToDate() == null
                        || !o.getDate().isAfter(request.getToDate()))
                .filter(o -> request.getKind() == null
                        || o.getKind().equals(request.getKind()))
                .filter(o -> request.getAccount() == null
                        || o.getAccount().equals(request.getAccount()))
                .filter(o -> request.getDescription() == null
                        || o.getDescription().toLowerCase()
                        .contains(request.getDescription().toLowerCase()))
                .map(OperationDto::from)
                .toList();
    }
}
