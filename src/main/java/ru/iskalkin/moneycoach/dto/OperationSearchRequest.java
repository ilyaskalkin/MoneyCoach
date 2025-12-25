package ru.iskalkin.moneycoach.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OperationSearchRequest {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Short kind;
    private Short account;
    private String description;
}
