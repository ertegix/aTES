package ru.ertegix.ates.accounting.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Table(name = "BILLING_CYCLE")
public class BillingCycle {

    @Id
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
}
