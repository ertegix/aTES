package ru.ertegix.ates.accounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ertegix.ates.accounting.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByTaskPublicId(UUID publicId);

    @Query(value = "select t from Task  t where t.status = 'NOT_DONE' " +
            "and t.assignedDate = now()")
    List<Task> findAssignedNotCompletedToday();

    @Query(value = "select t from Task  t where t.status = 'DONE' " +
            "and t.completedDate = now()")
    List<Task> findCompletedToday();
}
