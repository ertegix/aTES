package ru.ertegix.ates.accounting.scheduler;


import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ertegix.ates.accounting.email.EmailService;
import ru.ertegix.ates.accounting.model.Account;
import ru.ertegix.ates.accounting.model.BillingCycle;
import ru.ertegix.ates.accounting.model.Payment;
import ru.ertegix.ates.accounting.model.User;
import ru.ertegix.ates.accounting.repo.AccountRepository;
import ru.ertegix.ates.accounting.repo.BillingCycleRepository;
import ru.ertegix.ates.accounting.repo.PaymentRepository;
import ru.ertegix.ates.accounting.repo.UserRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class BillingCyclesProcessor {

    private final AccountRepository accountRepository;
    private final BillingCycleRepository billingCycleRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;

    @Scheduled(fixedRate = 1000 * 60 * 5L)
    public void process() {
        List<BillingCycle> cycles = billingCycleRepository.findAllByClosed(false);

        for (BillingCycle billingCycle: cycles) {
            if (billingCycle.isEnded()) {
                long balance = billingCycle.processTransactions();
                if (balance > 0) {
                    Payment payment = new Payment(
                            billingCycle.getId(),
                            billingCycle.getAccountId(),
                            balance
                    );
                    paymentRepository.saveAndFlush(payment);
                    billingCycle.close();
                    billingCycleRepository.saveAndFlush(billingCycle);
                    emailService.sentPaymentInfoToUser(payment);
                }
                Optional<Account> userAccount = accountRepository.findById(billingCycle.getAccountId());
                if (userAccount.isPresent()) {
                    Account account = userAccount.get();
                    account.addToBalance(balance);
                    accountRepository.saveAndFlush(account);
                }
            }
        }
    }
}
