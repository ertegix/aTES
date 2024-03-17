package ru.ertegix.ates.accounting.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PAYMENT")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    private Long billingCycleId;
    private Long accountId;
    private Long amount;

    public Payment(Long billingCycleId, Long accountId, Long amount) {
        this.billingCycleId = billingCycleId;
        this.accountId = accountId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "billingCycleId=" + billingCycleId +
                ", accountId=" + accountId +
                '}';
    }
}
