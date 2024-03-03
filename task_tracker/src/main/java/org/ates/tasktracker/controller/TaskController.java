package org.ates.tasktracker.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ates.tasktracker.model.Task;
import org.ates.tasktracker.repo.TaskRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskRepository taskRepository;

    @GetMapping("/")
    public List<Task> task() {
        return taskRepository.getAll();
    }
}