package ru.ertegix.ates.accounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ertegix.ates.accounting.model.Task;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByTaskPublicId(UUID publicId);
}
