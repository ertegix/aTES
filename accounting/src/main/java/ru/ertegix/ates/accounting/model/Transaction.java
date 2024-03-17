package ru.ertegix.ates.accounting.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.*;

@Getter
@Data
@NoArgsConstructor
@Entity
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long accountId;
    @ManyToOne
    private BillingCycle billingCycle;
    private Long income;
    private Long outcome;
    @Enumerated(value = EnumType.STRING)
    private TransactionType type;

    private LocalDateTime createDate;
    private String description;

    private Transaction(Long accountId, BillingCycle billingCycle,
                       Long income, Long outcome, TransactionType type,
                       String description
    )
    {
        this.accountId = accountId;
        this.billingCycle = billingCycle;
        this.income = income;
        this.outcome = outcome;
        this.type = type;
        this.createDate = LocalDateTime.now();
        this.description = description;
    }


    public static Transaction income(BillingCycle billingCycle, Task task) {
        return new Transaction(
                billingCycle.getAccountId(),
                billingCycle,
                task.getCompletionReward(),
                0L,
                TransactionType.INCOME,
                task.getDescription()
        );
    }

    public static Transaction outcome(BillingCycle billingCycle, Task task) {
        return new Transaction(
                billingCycle.getAccountId(),
                billingCycle,
                0L,
                task.getAssignCost(),
                TransactionType.OUTCOME,
                task.getDescription()
                );
    }
}

