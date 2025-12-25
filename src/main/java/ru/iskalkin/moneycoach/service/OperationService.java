package ru.iskalkin.moneycoach.service;

import ru.iskalkin.moneycoach.dto.OperationSearchRequest;
import ru.iskalkin.moneycoach.model.Operation;
import ru.iskalkin.moneycoach.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OperationService {

    private final OperationRepository repository;

    public Operation add(Operation operation) {
        operation.setId(null);
        return repository.save(operation);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Operation> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Operation> find(OperationSearchRequest request) {
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
                .toList();
    }
}
