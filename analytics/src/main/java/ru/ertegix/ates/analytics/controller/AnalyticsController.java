package ru.ertegix.ates.analytics.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ertegix.ates.analytics.model.Task;
import ru.ertegix.ates.analytics.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnalyticsController {

    private final TaskRepository taskRepository;

    @GetMapping("/all")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/taskWithHighestCost/lastDay")
    public Task taskWithHighestCostForTheLastDay() {
        return taskRepository.getTaskWithHighestCost(LocalDate.now().minusDays(1));
    }

    @GetMapping("/taskWithHighestCost/lastWeek")
    public Task taskWithHighestCostForTheLastWeek() {
        return taskRepository.getTaskWithHighestCost(LocalDate.now().minusDays(7));
    }
}
