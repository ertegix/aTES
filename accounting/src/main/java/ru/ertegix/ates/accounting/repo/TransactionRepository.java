package ru.ertegix.ates.accounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ertegix.ates.accounting.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccountIdAndCreateDate(Long accountId, LocalDate createDate);
    List<Transaction> findAllByBillingCycleId(Long billingCycleId);

}
