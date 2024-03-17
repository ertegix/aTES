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
    private Long accountId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean closed = false;
    @OneToMany(mappedBy = "billingCycle", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<Transaction> transactions = new ArrayList<>();

    public BillingCycle(Period period, Account account) {
        this.accountId = account.getId();
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusDays(period.getDays());
    }

    public void close() {
        this.closed = true;
    }

    public boolean isEnded() {
        LocalDate today = LocalDate.now();
        return (!startDate.isBefore(today) && !startDate.isEqual(today))
                || (!endDate.isAfter(today) && !endDate.isEqual(today));
    }

    public int processTransactions() {
        int totalIncome = 0;
        int totalOutcome = 0;

        for (Transaction t: transactions) {
            totalOutcome+=t.getOutcome();
            totalIncome+=t.getIncome();
        }

        return totalIncome - totalOutcome;
    }
}
