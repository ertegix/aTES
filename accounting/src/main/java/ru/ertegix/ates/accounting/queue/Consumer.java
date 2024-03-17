package ru.ertegix.ates.accounting.queue;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.ertegix.ates.accounting.model.BillingCycle;
import ru.ertegix.ates.accounting.model.Task;
import ru.ertegix.ates.accounting.model.Transaction;
import ru.ertegix.ates.accounting.model.User;
import ru.ertegix.ates.accounting.repo.BillingCycleRepository;
import ru.ertegix.ates.accounting.repo.TaskRepository;
import ru.ertegix.ates.common.Status;
import ru.ertegix.ates.common.TaskBeEventType;
import ru.ertegix.ates.event.TaskBusinessEvent_v1;
import ru.ertegix.ates.accounting.repo.UserRepository;
import ru.ertegix.ates.common.Role;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

import static ru.ertegix.ates.accounting.queue.TopicNames.*;

@Slf4j
@AllArgsConstructor
@Component
public class Consumer {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BillingCycleRepository billingCycleRepository;

    @KafkaListener(topics = USER_STREAM_TOPIC_NAME)
    public void receiveUserEvent(@Payload ru.ertegix.ates.event.UserEvent_v1 event) {
        log.info("Received user stream event: {}", event.getPublicId());
        Optional<User> user = userRepository.findByPublicId(UUID.fromString(event.publicId));
        if (user.isPresent()) {
            User userFound = user.get();
            userFound.setRole(Role.valueOf(event.role));
            userRepository.saveAndFlush(userFound);
        } else {
            userRepository.saveAndFlush(
                    new User(
                            UUID.fromString(event.publicId),
                            event.username,
                            Role.valueOf(event.role))
            );
        }
    }

    @KafkaListener(topics = TASK_STREAM_TOPIC_NAME)
    public void receiveTaskEvent(@Payload ru.ertegix.ates.event.TaskEvent_v1 taskEventV1) {
        log.info("Received task stream event: {}", taskEventV1.getPublicId());
        Optional<Task> task = taskRepository.findByTaskPublicId(UUID.fromString(taskEventV1.getPublicId()));
        if (task.isPresent()) {
            Task taskForUpdate = task.get();
            taskForUpdate.setDescription(taskEventV1.getDescription());
        } else {
            createTask(
                    taskEventV1.publicId,
                    taskEventV1.getUserPublicId(),
                    taskEventV1.getDescription(),
                    Status.NOT_DONE
            );
        }
    }

    @KafkaListener(topics = TASK_TOPIC_NAME)
    public void receiveTaskBusinessEvent(TaskBusinessEvent_v1 eventV1) {
        createUserIfNotExist(UUID.fromString(eventV1.getUserPublicId()));
        TaskBeEventType eventType = TaskBeEventType.valueOf(eventV1.getEventType());
        switch (eventType) {
            case CREATED:
                receiveTaskCreated(eventV1);
                break;
            case ASSIGNED:
                receiveTaskAssigned(eventV1);
                break;
            case COMPLETED:
                receiveTaskCompleted(eventV1);
                break;
            default:
                log.error("Business event is of incompatible type {}",
                        eventV1.getEventType());
                throw new IllegalArgumentException();

        }
    }

    private void receiveTaskCreated(TaskBusinessEvent_v1 taskEventV1) {
        log.info("Received task created: {}", taskEventV1.getPublicId());
        Optional<Task> task = taskRepository.findByTaskPublicId(UUID.fromString(taskEventV1.getPublicId()));
        if (task.isPresent()) {
            addTransactionToUsersAccount(
                    task.get().getUserPublicId(),
                    task.get(),
                    false);
        } else {
            Task newTask = createTask(
                    taskEventV1.publicId,
                    taskEventV1.getUserPublicId(),
                    taskEventV1.getDescription(),
                    Status.NOT_DONE
            );

            addTransactionToUsersAccount(
                    newTask.getUserPublicId(),
                    newTask,
                    false
            );
        }
    }

    private void receiveTaskAssigned(TaskBusinessEvent_v1 taskEventV1) {
        log.info("Received task assigned: {}", taskEventV1.getPublicId());
        Optional<Task> task = taskRepository.findByTaskPublicId(
                UUID.fromString(taskEventV1.getPublicId())
        );

        if (task.isPresent()) {
            Task taskForUpdate = task.get();
            taskForUpdate.setUserPublicId(UUID.fromString(taskEventV1.getUserPublicId()));
            addTransactionToUsersAccount(task.get().getUserPublicId(), task.get(), false);
        } else {
            Task newTask = createTask(
                    taskEventV1.publicId,
                    taskEventV1.getUserPublicId(),
                    "NOT PRESENT YET",
                    Status.NOT_DONE
            );

            addTransactionToUsersAccount(
                    newTask.getUserPublicId(),
                    newTask,
                    false
            );
        }
    }

    private void receiveTaskCompleted(TaskBusinessEvent_v1 taskEventV1) {
        log.info("Received task completed: {}", taskEventV1.getPublicId());
        Optional<Task> task = taskRepository.findByTaskPublicId(
                UUID.fromString(taskEventV1.getPublicId())
        );

        if (task.isPresent()) {
            Task taskForUpdate = task.get();
            taskForUpdate.setUserPublicId(UUID.fromString(taskEventV1.getUserPublicId()));
            taskForUpdate.setCompletedDate(LocalDate.now());
            addTransactionToUsersAccount(task.get().getUserPublicId(), task.get(), true);
        } else {
            Task newTask = createTask(
                    taskEventV1.publicId,
                    taskEventV1.getUserPublicId(),
                    "NOT PRESENT YET",
                    Status.DONE
            );

            addTransactionToUsersAccount(
                    newTask.getUserPublicId(),
                    newTask,
                    true
            );
        }
    }


    private Task createTask(String taskPublicId, String userPublicId, String description,
                            Status status) {
        Task task = new Task(
                UUID.fromString(taskPublicId),
                UUID.fromString(userPublicId),
                description,
                status);
        taskRepository.saveAndFlush(task);
        return task;
    }

    private void addTransactionToUsersAccount(UUID userPublicId, Task task, boolean income) {
        Optional<BillingCycle> billingCycle = billingCycleRepository.findActualByUserPublicId(
                userPublicId, LocalDate.now()
        );

        if (billingCycle.isPresent()) {
            BillingCycle updating = billingCycle.get();
            Transaction newTransaction = income
                    ? Transaction.income(updating, task)
                    : Transaction.outcome(updating, task);
            updating.getTransactions().add(newTransaction);
            billingCycleRepository.saveAndFlush(updating);
        } else {
            BillingCycle newBillingCycle = new BillingCycle(
                    Period.ofDays(1),
                    userPublicId
            );
            Transaction newTransaction = income
                    ? Transaction.income(newBillingCycle, task)
                    : Transaction.outcome(newBillingCycle, task);
            newBillingCycle.getTransactions().add(newTransaction);
            billingCycleRepository.saveAndFlush(newBillingCycle);
        }
    }


    private void createUserIfNotExist(UUID userPublicId) {
        Optional<User> user = userRepository.findByPublicId(userPublicId);
        if (user.isEmpty()) {
            userRepository.saveAndFlush(
                    new User(
                            userPublicId,
                            "<WILL BE SET LATER>",
                            Role.UNKNOWN)
            );
        }
    }
}
