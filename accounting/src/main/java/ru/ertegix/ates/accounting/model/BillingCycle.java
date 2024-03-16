package ru.ertegix.ates.accounting.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "BILLING_CYCLE")
public class BillingCycle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private UUID userPublicId;
    private LocalDate startDate;
    private LocalDate endDate;
    @OneToMany(mappedBy = "billingCycle", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<Transaction> transactions = new ArrayList<>();

    public BillingCycle(Period period, UUID userPublicId) {
        this.userPublicId = userPublicId;
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusDays(period.getDays());
    }
}
