package org.ates.tasktracker.model;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TASK")
public class Task {

    @Id
    private String id;
    private String description;
}
