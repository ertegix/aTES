package ru.ertegix.ates.accounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ertegix.ates.accounting.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
