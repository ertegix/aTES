package ru.ertegix.ates.tasktracker.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Table
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TASK")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private UUID taskPublicId;
    @Column(nullable = false)
    private UUID userPublicId;
    private String description;
    private Status status;

    public Task(UUID userPublicId, String description, Status status) {
        this.taskPublicId = UUID.randomUUID();
        this.userPublicId = userPublicId;
        this.description = description;
        this.status = status;
    }
}
