package ru.ertegix.ates.tasktracker.repo;

import ru.ertegix.ates.tasktracker.model.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskRepository {

    private final List<Task> tasks = new ArrayList<>();

    public List<Task> getAll() {
        return tasks;
    }

}
