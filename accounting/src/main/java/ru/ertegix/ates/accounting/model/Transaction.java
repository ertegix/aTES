package ru.ertegix.ates.accounting.model;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@Entity
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    private Long id;
    private UUID userPublicId;
    private Long billingCycleId;
    private BigDecimal debit;
    private BigDecimal credit;

    public Transaction(UUID userPublicId, Long billingCycleId, BigDecimal debit, BigDecimal credit) {
        this.userPublicId = userPublicId;
        this.billingCycleId = billingCycleId;
        this.debit = debit;
        this.credit = credit;
    }
}

