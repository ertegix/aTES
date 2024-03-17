package ru.ertegix.ates.analytics.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime completionDate;
    private Long completionReward;
    private Long assignmentCost;
    private UUID taskPublicId;
    private boolean isCompleted;

    public Task(LocalDateTime completionDate,
                Long completionReward,
                Long assignmentCost,
                UUID taskPublicId)
    {
        this.completionDate = completionDate;
        this.completionReward = completionReward;
        this.assignmentCost = assignmentCost;
        this.taskPublicId = taskPublicId;
    }

    public void changePrices(Long assignmentCost, Long completionReward) {
        if (assignmentCost < 0) {
            this.assignmentCost = assignmentCost;
        }
        if (completionReward < 0) {
            this.completionReward = completionReward;
        }
    }
}
