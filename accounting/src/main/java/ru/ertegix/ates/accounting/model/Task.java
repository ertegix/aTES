package ru.ertegix.ates.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ertegix.ates.common.Status;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Table
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TASK")
public class Task {

    private final static ThreadLocalRandom RANDOMIZER = ThreadLocalRandom.current();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private UUID taskPublicId;
    @Setter
    @Column(nullable = false)
    private UUID userPublicId;
    @Setter
    private String description;
    private Integer assignCost;
    private Integer completionReward;

    public Task(UUID userPublicId, String description) {
        this.taskPublicId = UUID.randomUUID();
        this.userPublicId = userPublicId;
        this.description = description;
        this.assignCost = RANDOMIZER.nextInt(10, 20);
        this.completionReward = RANDOMIZER.nextInt(20, 40);
    }
}

