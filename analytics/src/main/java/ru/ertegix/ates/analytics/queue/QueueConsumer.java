package ru.ertegix.ates.analytics.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.ertegix.ates.analytics.model.Task;
import ru.ertegix.ates.analytics.repository.TaskRepository;
import ru.ertegix.ates.common.Role;
import ru.ertegix.ates.common.Status;
import ru.ertegix.ates.common.TaskBeEventType;
import ru.ertegix.ates.event.UserEvent_v1;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static ru.ertegix.ates.analytics.queue.TopicsNames.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class QueueConsumer {

    private final TaskRepository taskRepository;

    @KafkaListener(topics = TASK_STREAM_TOPIC_NAME)
    public void receiveTaskEvent(@Payload ru.ertegix.ates.event.TaskEvent_v1 taskEventV1) {
        log.info("Received task stream event: {}", taskEventV1.getPublicId());
        Optional<Task> task = taskRepository.findByTaskPublicId(UUID.fromString(taskEventV1.getPublicId()));
        if (task.isEmpty()) {
            Task newTask = new Task(
                    null,
                    -1L,
                    -1L,
                    UUID.fromString(taskEventV1.getPublicId())
                    );

            taskRepository.saveAndFlush(newTask);
        }
    }

    @KafkaListener(topics = TASK_TOPIC_NAME)
    public void receive(@Payload ru.ertegix.ates.event.TaskBusinessEvent_v1 event)  {
        log.info("Received task info: {}", event.getPublicId());

        LocalDateTime completionTime = null;
        if (TaskBeEventType.valueOf(event.eventType) == TaskBeEventType.COMPLETED) {
            completionTime = LocalDateTime.parse(event.eventDateTime);
        }

        Optional<Task> task = taskRepository.findByTaskPublicId(UUID.fromString(event.getPublicId()));
        if (task.isPresent()) {
            Task taskFound = task.get();
            taskFound.setCompletionDate(completionTime);
            taskRepository.saveAndFlush(taskFound);
        } else {
            Task newTask = new Task(
                    completionTime,
                    -1L,
                    -1L,
                    UUID.fromString(event.getPublicId())
            );
            taskRepository.saveAndFlush(newTask);
        }
    }

    @KafkaListener(topics = PRICES_TOPIC_NAME)
    public void receivePrices(ru.ertegix.ates.event.PricesEvent_v1 eventV1) {
        log.info("Received price info {}", eventV1.taskPublicId);
        Optional<Task> task = taskRepository.findByTaskPublicId(
                UUID.fromString(eventV1.getTaskPublicId()));

        if (task.isPresent()) {
            Task foundTask = task.get();
            foundTask.changePrices(
                    eventV1.getAssignmentCost(),
                    eventV1.getCompletionReward());
        } else {
            Task newTask = new Task(
                    null,
                    eventV1.getCompletionReward(),
                    eventV1.getAssignmentCost(),
                    UUID.fromString(eventV1.getTaskPublicId())
            );
            taskRepository.saveAndFlush(newTask);
        }
    }
}
