package ru.ertegix.ates.tasktracker.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ertegix.ates.tasktracker.model.Task;
import ru.ertegix.ates.tasktracker.repo.TaskRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskRepository taskRepository;

    @GetMapping("/")
    public @ResponseBody List<Task> task() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("task-id","first_task"));
        tasks.addAll(taskRepository.getAll());
        return tasks;
    }
}
