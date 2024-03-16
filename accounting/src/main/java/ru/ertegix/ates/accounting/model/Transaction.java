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
    private Integer debit;
    private Integer credit;

    public Transaction(UUID userPublicId, BillingCycle billingCycle, Integer debit, Integer credit) {
        this.userPublicId = userPublicId;
        this.billingCycle = billingCycle;
        this.debit = debit;
        this.credit = credit;
    }


    public static Transaction income(BillingCycle billingCycle, Task task) {
        return new Transaction(
                task.getUserPublicId(),
                billingCycle,
                task.getCompletionReward(),
                0
        );
    }

    public static Transaction outcome(BillingCycle billingCycle, Task task) {
        return new Transaction(
                task.getUserPublicId(),
                billingCycle,
                0,
                task.getAssignCost()
                );
    }
}

