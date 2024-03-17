package ru.ertegix.ates.accounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ertegix.ates.accounting.model.BillingCycle;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillingCycleRepository extends JpaRepository<BillingCycle, Long> {

    List<BillingCycle> findAllByClosed(Boolean closed);

    @Query(value = "select b from BillingCycle b where b.accountId = :accountId" +
            " and b.startDate <= :date and b.endDate >= :date")
    Optional<BillingCycle> findActualByAccountId(
            @Param("accountId") Long userPublicId,
            @Param("date") LocalDate date);
}
