package ru.iskalkin.moneycoach.dto;

import lombok.Data;
import ru.iskalkin.moneycoach.model.Operation;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OperationDto {

    private Long id;
    private LocalDate date;
    private BigDecimal amount;
    private String description;
    private Short kind;
    private Short account;
    private Boolean storned;

    public static OperationDto from(Operation operation) {
        OperationDto dto = new OperationDto();
        dto.setId(operation.getId());
        dto.setDate(operation.getDate());
        dto.setAmount(operation.getAmount());
        dto.setDescription(operation.getDescription());
        dto.setKind(operation.getKind());
        dto.setAccount(operation.getAccount());
        dto.setStorned(operation.getStorned());
        return dto;
    }

    public Operation toEntity() {
        return Operation.builder()
                .id(this.id)
                .date(this.date)
                .amount(this.amount)
                .description(this.description)
                .kind(this.kind)
                .account(this.account)
                .storned(this.storned)
                .build();
    }
}
