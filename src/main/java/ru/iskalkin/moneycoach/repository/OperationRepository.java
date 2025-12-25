package ru.iskalkin.moneycoach.repository;

import ru.iskalkin.moneycoach.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

    List<Operation> findByDateBetween(LocalDate from, LocalDate to);

    List<Operation> findByKind(Short kind);

    List<Operation> findByAccount(Short account);

    List<Operation> findByDescriptionContainingIgnoreCase(String description);
}
