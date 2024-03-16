package ru.ertegix.ates.accounting.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.ertegix.ates.accounting.model.Transaction;

import java.util.UUID;

@Getter
@Setter
public class TransactionDto {

    private final UUID userPublicId;
    private final Long billingCycleId;
    private final Integer debit;
    private final Integer credit;

    public TransactionDto(Transaction transaction) {
        this.userPublicId = transaction.getUserPublicId();
        this.debit = transaction.getDebit();
        this.credit = transaction.getCredit();
        this.billingCycleId = transaction.getBillingCycle().getId();
    }
}
