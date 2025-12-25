package ru.iskalkin.moneycoach.controller;

import ru.iskalkin.moneycoach.dto.OperationSearchRequest;
import ru.iskalkin.moneycoach.model.Operation;
import ru.iskalkin.moneycoach.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService service;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Operation add(@RequestBody Operation operation) {
        return service.add(operation);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/all")
    public List<Operation> getAll() {
        return service.getAll();
    }

    @PostMapping("/find")
    public List<Operation> find(@RequestBody OperationSearchRequest request) {
        return service.find(request);
    }
}
