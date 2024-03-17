package ru.ertegix.ates.accounting.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

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
    private UUID userPublicId;
    @ManyToOne
    private BillingCycle billingCycle;
    private Integer income;
    private Integer outcome;
    @Enumerated(value = EnumType.STRING)
    private TransactionType type;

    public Transaction(UUID userPublicId, BillingCycle billingCycle, Integer income, Integer credit, TransactionType type)
    {
        this.userPublicId = userPublicId;
        this.billingCycle = billingCycle;
        this.income = income;
        this.outcome = credit;
        this.type = type;
    }


    public static Transaction income(BillingCycle billingCycle, Task task) {
        return new Transaction(
                task.getUserPublicId(),
                billingCycle,
                task.getCompletionReward(),
                0,
                TransactionType.INCOME
        );
    }

    public static Transaction outcome(BillingCycle billingCycle, Task task) {
        return new Transaction(
                task.getUserPublicId(),
                billingCycle,
                0,
                task.getAssignCost(),
                TransactionType.OUTCOME
                );
    }
}

