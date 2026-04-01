package ru.iskalkin.moneycoach.controller;

import ru.iskalkin.moneycoach.dto.OperationDto;
import ru.iskalkin.moneycoach.dto.OperationSearchRequest;
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
    public OperationDto add(@RequestBody OperationDto dto) {
        return service.add(dto);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }

    @GetMapping("/all")
    public List<OperationDto> getAll() {
        return service.getAll();
    }

    @PostMapping("/find")
    public List<OperationDto> find(@RequestBody OperationSearchRequest request) {
        return service.find(request);
    }

    @PatchMapping("/{id}/storno")
    public OperationDto storno(@PathVariable("id") Long id) {
        return service.storno(id);
    }
}
