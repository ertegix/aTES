package ru.ertegix.ates.accounting.queue;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class QueueMessageSender {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void sendPrice(ru.ertegix.ates.event.PricesEvent_v1 pricesEventV1) {
        kafkaTemplate.send(
                TopicNames.PRICES_TOPIC_NAME,
                UUID.randomUUID().toString(),
                pricesEventV1);
    }
}
