package ru.ertegix.ates.accounting.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ertegix.ates.accounting.model.Payment;

@Slf4j
@Component
public class EmailService {

    public void sentPaymentInfoToUser(Payment payment) {
        log.info("Sending payment to user: {}", payment);
    }
}
