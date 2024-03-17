package ru.ertegix.ates.accounting.dto;

import lombok.Getter;
import lombok.Setter;
import ru.ertegix.ates.accounting.model.Transaction;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransactionDto {

    private final Long accountId;
    private final Long billingCycleId;
    private final Long income;
    private final Long outcome;
    private final String description;
    private final LocalDateTime createdAt;

    public TransactionDto(Transaction transaction) {
        this.accountId = transaction.getAccountId();
        this.income = transaction.getIncome();
        this.outcome = transaction.getOutcome();
        this.billingCycleId = transaction.getBillingCycle().getId();
        this.description = transaction.getDescription();
        this.createdAt = transaction.getCreateDate();
    }
}
