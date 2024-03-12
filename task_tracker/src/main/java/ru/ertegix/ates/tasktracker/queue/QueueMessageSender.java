package ru.ertegix.ates.tasktracker.queue;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class QueueMessageSender {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(Object event, String topic) {
        kafkaTemplate.send(topic, event);
    }
}
