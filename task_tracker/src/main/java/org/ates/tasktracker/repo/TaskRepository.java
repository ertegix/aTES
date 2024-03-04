package org.ates.tasktracker.repo;

import org.ates.tasktracker.model.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskRepository {

    public List<Task> getAll() {
        return new ArrayList<>();
    }

}
