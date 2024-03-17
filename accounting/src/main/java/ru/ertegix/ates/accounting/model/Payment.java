package ru.ertegix.ates.accounting.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "PAYMENT")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    private Long billingCycleId;
    private UUID userPublicId;
    private Integer amount;

    public Payment(Long billingCycleId, UUID userPublicId, Integer amount) {
        this.billingCycleId = billingCycleId;
        this.userPublicId = userPublicId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "billingCycleId=" + billingCycleId +
                ", userPublicId=" + userPublicId +
                '}';
    }
}
