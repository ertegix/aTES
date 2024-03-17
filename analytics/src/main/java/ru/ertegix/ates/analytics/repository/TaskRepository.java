package ru.ertegix.ates.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ertegix.ates.analytics.model.Task;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByTaskPublicId(UUID taskPublicId);

    @Query(value = "select t from Task t where t.completionDate >= :date order by t.assignmentCost desc")
    Task getTaskWithHighestCost(@Param("date") LocalDate date);
}
