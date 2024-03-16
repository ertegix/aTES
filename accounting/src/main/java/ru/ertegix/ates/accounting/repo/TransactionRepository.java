package ru.ertegix.ates.accounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ertegix.ates.accounting.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByUserPublicId(UUID userPubicId);
    List<Transaction> findAllByBillingCycleId(Long billingCycleId);

}
