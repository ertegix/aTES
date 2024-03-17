package ru.ertegix.ates.tasktracker.queue;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.ertegix.ates.tasktracker.event.TaskBeEvent_v1;

import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class QueueMessageSender {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCudMessage(Object event) {
        log.info("sending CUD message: {}", event);
        kafkaTemplate.send(
                TopicsNames.TASK_STREAM_TOPIC_NAME,
                UUID.randomUUID().toString(),
                event);
    }

    public <T> void sendBeMessage(TaskBeEvent_v1 event) {
        log.info("sending BE message: {}", event);
        kafkaTemplate.send(
                TopicsNames.TASK_TOPIC_NAME,
                UUID.randomUUID().toString(),
                event.getContent());
    }
}
