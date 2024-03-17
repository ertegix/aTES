package ru.ertegix.ates.queue;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class MessageSender {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(Object event, String topic) {
        kafkaTemplate.send(
                topic,
                UUID.randomUUID().toString(),
                event);
    }
}