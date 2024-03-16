package ru.ertegix.ates.tasktracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ertegix.ates.tasktracker.model.Status;
import ru.ertegix.ates.tasktracker.model.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Override
    List<Task> findAll();

    List<Task> findAllByStatus(Status status);
}
