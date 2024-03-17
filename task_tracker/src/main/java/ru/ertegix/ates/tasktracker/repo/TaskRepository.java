package ru.ertegix.ates.tasktracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ertegix.ates.tasktracker.model.Status;
import ru.ertegix.ates.tasktracker.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Override
    List<Task> findAll();

    List<Task> findAllByStatus(Status status);

    List<Task> findAllByUserPublicId(UUID userPublicId);

    Optional<Task> findByTaskPublicId(UUID taskPublicId);

}
