package ru.ertegix.ates.tasktracker.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;
import java.util.regex.Pattern;

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

    private String jiraId;

    public Task(UUID userPublicId, String description, Status status) {
        this.taskPublicId = UUID.randomUUID();
        this.userPublicId = userPublicId;
        this.description = description;
        this.status = status;
        this.jiraId = null;
    }

    public Task(UUID userPublicId, String description, Status status, String jiraId) {
        this.taskPublicId = UUID.randomUUID();
        this.userPublicId = userPublicId;
        this.description = description;
        this.status = status;
        this.jiraId = jiraId;
    }
}
