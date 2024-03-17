package ru.ertegix.ates.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ertegix.ates.common.Status;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Setter
@Getter
@Table
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {

    private final static ThreadLocalRandom RANDOMIZER = ThreadLocalRandom.current();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private UUID taskPublicId;
    @Column(nullable = false)
    private UUID userPublicId;
    private String description;
    private String jiraId;
    private Long assignCost;
    private Long completionReward;
    private LocalDate assignedDate;
    private LocalDate completedDate;
    private Status status;

    public Task(UUID taskPublicId, UUID userPublicId, String description, Status status) {
        this.taskPublicId = taskPublicId;
        this.userPublicId = userPublicId;
        this.description = description;
        this.assignCost = RANDOMIZER.nextLong(10, 20);
        this.completionReward = RANDOMIZER.nextLong(20, 40);
        this.assignedDate = LocalDate.now();
        this.status = status;
    }

    public Task(UUID taskPublicId, UUID userPublicId, String description, String jiraId, Status status)
    {
        this.taskPublicId = taskPublicId;
        this.userPublicId = userPublicId;
        this.description = description;
        this.assignCost = RANDOMIZER.nextLong(10, 20);
        this.completionReward = RANDOMIZER.nextLong(20, 40);
        this.assignedDate = LocalDate.now();
        this.status = status;
        this.jiraId = jiraId;
    }
}

