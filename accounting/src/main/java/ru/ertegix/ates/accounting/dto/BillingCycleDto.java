package ru.ertegix.ates.accounting.dto;

import lombok.Getter;
import lombok.Setter;
import ru.ertegix.ates.accounting.model.BillingCycle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Setter
@Getter
public class BillingCycleDto {

    private Long accountId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TransactionDto> transactions = new ArrayList<>();

    public BillingCycleDto(BillingCycle billingCycle) {
        this.accountId = billingCycle.getAccountId();
        this.startDate = billingCycle.getStartDate();
        this.endDate = billingCycle.getEndDate();
        this.transactions = billingCycle.getTransactions()
                .stream().map(TransactionDto::new)
                .collect(Collectors.toList());
    }
}
