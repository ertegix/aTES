package ru.ertegix.ates.tasktracker.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.ertegix.ates.common.Role;
import ru.ertegix.ates.event.TaskBusinessEvent_v1;
import ru.ertegix.ates.event.TaskEvent_v1;
import ru.ertegix.ates.tasktracker.event.TaskBeEventType;
import ru.ertegix.ates.tasktracker.event.TaskBeEvent_v1;
import ru.ertegix.ates.tasktracker.event.TaskBeEvent_v2;
import ru.ertegix.ates.tasktracker.model.Status;
import ru.ertegix.ates.tasktracker.model.Task;
import ru.ertegix.ates.tasktracker.model.User;
import ru.ertegix.ates.tasktracker.queue.QueueMessageSender;
import ru.ertegix.ates.tasktracker.repo.TaskRepository;
import org.springframework.http.MediaType;
import ru.ertegix.ates.tasktracker.repo.UserRepository;
import ru.ertegix.ates.tasktracker.request.CreateTaskRequest;
import ru.ertegix.ates.tasktracker.security.JwtUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final QueueMessageSender messageSender;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public @ResponseBody List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @PreAuthorize("hasAuthority('WORKER') or hasAuthority('ADMIN')")
    @GetMapping("/myTasks")
    public @ResponseBody List<Task> getWorkersTasks(Authentication authentication)
    {
        UUID userPublicId = ((JwtUser) authentication.getPrincipal()).getPublicId();
        if (userPublicId == null) {
            String username = ((JwtUser) authentication.getPrincipal()).getUsername();
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                userPublicId = user.get().getPublicId();
            }
        }

        return taskRepository.findAllByUserPublicId(userPublicId);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','WORKER')")
    @PostMapping("/create")
    public @ResponseBody Task createTask(@RequestBody CreateTaskRequest request) {
        User user = userRepository.getRandomUser();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There are no users found");
        }

        String jiraId =  request.getJiraId() == null
                ? extractJiraId(request.getDescription())
                : request.getJiraId();

        Task task = new Task(
                user.getPublicId(),
                request.getDescription(),
                Status.NOT_DONE,
                jiraId
        );
        taskRepository.saveAndFlush(task);
        TaskEvent_v1 taskEvent = new TaskEvent_v1();
        taskEvent.setPublicId(task.getTaskPublicId().toString());
        taskEvent.setDescription(task.getDescription());
        taskEvent.setUserPublicId(user.getPublicId().toString());
        messageSender.sendCudMessage(taskEvent);
        messageSender.sendBeMessage(
                new TaskBeEvent_v1(
                        new ru.ertegix.ates.event.TaskBusinessEvent_v1(
                                TaskBeEventType.CREATED.name(),
                                task.getTaskPublicId().toString(),
                                task.getDescription(),
                                task.getUserPublicId().toString(),
                                LocalDateTime.now().toString()
                        )
                )
        );

        messageSender.sendBeMessage(
                new TaskBeEvent_v2(
                        new ru.ertegix.ates.event.TaskBusinessEvent_v2(
                                TaskBeEventType.CREATED.name(),
                                task.getTaskPublicId().toString(),
                                task.getJiraId(),
                                task.getDescription(),
                                task.getUserPublicId().toString(),
                                LocalDateTime.now().toString()
                        )
                )
        );
        return task;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @PutMapping("/assignTasks")
    public void assignTasks() {
        List<Task> tasks = taskRepository.findAllByStatus(Status.NOT_DONE);
        List<User> users = userRepository.findAllByRole(Role.WORKER);
        for (Task task: tasks) {
            int random = ThreadLocalRandom.current().nextInt(0, users.size() - 1);
            User user = users.get(random);
            task.setUserPublicId(user.getPublicId());
            taskRepository.saveAndFlush(task);
            TaskBusinessEvent_v1 taskAssigned = new TaskBusinessEvent_v1();
            taskAssigned.setUserPublicId(user.getPublicId().toString());
            messageSender.sendBeMessage(
                    new TaskBeEvent_v1(
                            new ru.ertegix.ates.event.TaskBusinessEvent_v1(
                                    TaskBeEventType.ASSIGNED.name(),
                                    task.getTaskPublicId().toString(),
                                    task.getDescription(),
                                    task.getUserPublicId().toString(),
                                    LocalDateTime.now().toString()
                            )
                    )
            );

            String jiraId =  task.getJiraId() == null
                    ? extractJiraId(task.getDescription())
                    : task.getJiraId();

            messageSender.sendBeMessage(
                    new TaskBeEvent_v2(
                            new ru.ertegix.ates.event.TaskBusinessEvent_v2(
                                    TaskBeEventType.ASSIGNED.name(),
                                    task.getTaskPublicId().toString(),
                                    jiraId,
                                    task.getDescription(),
                                    task.getUserPublicId().toString(),
                                    LocalDateTime.now().toString()
                            )
                    )
            );
        }
    }

    @PreAuthorize("hasAnyAuthority('WORKER')")
    @PutMapping("/complete/{id}")
    public void completeTask(@PathVariable String id) {
        Optional<Task> task = taskRepository.findByTaskPublicId(UUID.fromString(id));

        if (task.isPresent()) {
            Task foundTask = task.get();
            foundTask.setStatus(Status.DONE);
            taskRepository.saveAndFlush(foundTask);

            messageSender.sendBeMessage(
                    new TaskBeEvent_v1(
                            new ru.ertegix.ates.event.TaskBusinessEvent_v1(
                                    TaskBeEventType.COMPLETED.name(),
                                    foundTask.getTaskPublicId().toString(),
                                    foundTask.getDescription(),
                                    foundTask.getUserPublicId().toString(),
                                    LocalDateTime.now().toString()
                            )
                    )
            );

            String jiraId =  foundTask.getJiraId() == null
                    ? extractJiraId(foundTask.getDescription())
                    : foundTask.getJiraId();

            messageSender.sendBeMessage(
                    new TaskBeEvent_v2(
                            new ru.ertegix.ates.event.TaskBusinessEvent_v2(
                                    TaskBeEventType.COMPLETED.name(),
                                    foundTask.getTaskPublicId().toString(),
                                    jiraId,
                                    foundTask.getDescription(),
                                    foundTask.getUserPublicId().toString(),
                                    LocalDateTime.now().toString()
                            )
                    )
            );
        }
    }


    // todo remove later
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/allUsers")
    public List<User> users() {
        return userRepository.findAll();
    }


    private String extractJiraId(String description) {
        boolean matches = Pattern.compile("\\[.+\\].+").matcher(description).matches();

        if (matches) {
            var ind2 = description.indexOf("]");
            return description.substring(0, ind2);
        } else {
            return description;
        }
    }

}
