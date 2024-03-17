package ru.ertegix.ates.accounting.queue;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.ertegix.ates.accounting.model.*;
import ru.ertegix.ates.accounting.repo.AccountRepository;
import ru.ertegix.ates.accounting.repo.BillingCycleRepository;
import ru.ertegix.ates.accounting.repo.TaskRepository;
import ru.ertegix.ates.common.Status;
import ru.ertegix.ates.common.TaskBeEventType;
import ru.ertegix.ates.event.PricesEvent_v1;
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

    private final QueueMessageSender messageSender;

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
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
    public void receiveTaskEventStream(@Payload ru.ertegix.ates.event.TaskEvent_v2 event) {
        log.info("Received task stream event: {}", event.getPublicId());
        Optional<Task> task = taskRepository.findByTaskPublicId(UUID.fromString(event.getPublicId()));
        if (task.isPresent()) {
            Task taskForUpdate = task.get();
            taskForUpdate.setJiraId(event.getJiraId());
            taskForUpdate.setDescription(event.getDescription());
        } else {
            createTask(
                    event.publicId,
                    event.getUserPublicId(),
                    event.getDescription(),
                    Status.NOT_DONE,
                    event.getJiraId()
            );
        }
    }

    @KafkaListener(topics = TASK_TOPIC_NAME)
    public void receiveTaskBusinessEvent_v2(ru.ertegix.ates.event.TaskBusinessEvent_v2 eventV1) {
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

    private void receiveTaskCreated(ru.ertegix.ates.event.TaskBusinessEvent_v2 event) {
        log.info("Received task created: {}", event.getPublicId());
        Optional<Task> task = taskRepository.findByTaskPublicId(UUID.fromString(event.getPublicId()));
        if (task.isPresent()) {
            addTransactionToUsersAccount(
                    task.get().getUserPublicId(),
                    task.get(),
                    false);
        } else {
            Task newTask = createTask(
                    event.publicId,
                    event.getUserPublicId(),
                    event.getDescription(),
                    Status.NOT_DONE,
                    event.getJiraId()
            );

            addTransactionToUsersAccount(
                    newTask.getUserPublicId(),
                    newTask,
                    false
            );
        }
    }

    private void receiveTaskAssigned(ru.ertegix.ates.event.TaskBusinessEvent_v2 event) {
        log.info("Received task assigned: {}", event.getPublicId());
        Optional<Task> task = taskRepository.findByTaskPublicId(
                UUID.fromString(event.getPublicId())
        );

        if (task.isPresent()) {
            Task taskForUpdate = task.get();
            taskForUpdate.setUserPublicId(UUID.fromString(event.getUserPublicId()));
            taskForUpdate.setJiraId(event.getJiraId());
            addTransactionToUsersAccount(task.get().getUserPublicId(), task.get(), false);
        } else {
            Task newTask = createTask(
                    event.publicId,
                    event.getUserPublicId(),
                    "NOT PRESENT YET",
                    Status.NOT_DONE,
                    event.getJiraId()
            );

            addTransactionToUsersAccount(
                    newTask.getUserPublicId(),
                    newTask,
                    false
            );
        }
    }

    private void receiveTaskCompleted(ru.ertegix.ates.event.TaskBusinessEvent_v2 event) {
        log.info("Received task completed: {}", event.getPublicId());
        Optional<Task> task = taskRepository.findByTaskPublicId(
                UUID.fromString(event.getPublicId())
        );

        if (task.isPresent()) {
            Task taskForUpdate = task.get();
            taskForUpdate.setUserPublicId(UUID.fromString(event.getUserPublicId()));
            taskForUpdate.setCompletedDate(LocalDate.now());
            addTransactionToUsersAccount(task.get().getUserPublicId(), task.get(), true);
        } else {
            Task newTask = createTask(
                    event.publicId,
                    event.getUserPublicId(),
                    "NOT PRESENT YET",
                    Status.DONE,
                    event.getJiraId()
            );

            addTransactionToUsersAccount(
                    newTask.getUserPublicId(),
                    newTask,
                    true
            );
        }
    }


    private Task createTask(String taskPublicId, String userPublicId, String description,
                            Status status, String jiraId)
    {


        Task task = new Task(
                UUID.fromString(taskPublicId),
                UUID.fromString(userPublicId),
                description,
                jiraId,
                status);
        taskRepository.saveAndFlush(task);
        messageSender.sendPrice(new PricesEvent_v1(
                task.getTaskPublicId().toString(),
                task.getCompletionReward(),
                task.getAssignCost()
        ));
        return task;
    }

    private void addTransactionToUsersAccount(UUID userPublicId, Task task, boolean income) {
        Account userAccount = accountRepository.findByUserPublicId(userPublicId)
                .orElseGet(() -> {
                    User newUser = new User(userPublicId, "<UNKNOWN>", Role.UNKNOWN);
                    userRepository.saveAndFlush(newUser);
                    return newUser.getAccount();
                });
        Optional<BillingCycle> billingCycle = billingCycleRepository.findActualByAccountId(
                userAccount.getId(), LocalDate.now()
        );

        if (billingCycle.isPresent()) {
            BillingCycle updating = billingCycle.get();
            Transaction newTransaction = income
                    ? Transaction.income(updating, task)
                    : Transaction.outcome(updating, task);
            updating.getTransactions().add(newTransaction);
            billingCycleRepository.saveAndFlush(updating);
        } else {
            Optional<Account> account = accountRepository.findByUserPublicId(userPublicId);
            BillingCycle newBillingCycle;
            if (account.isPresent()) {
                newBillingCycle = new BillingCycle(
                        Period.ofDays(1),
                        account.get()
                );
            } else {
                User newUser = new User(userPublicId, "<UNKNOWN>", Role.UNKNOWN);
                userRepository.saveAndFlush(newUser);
                Account newAccount = newUser.getAccount();
                newBillingCycle = new BillingCycle(Period.ofDays(1), newAccount);
            }

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
