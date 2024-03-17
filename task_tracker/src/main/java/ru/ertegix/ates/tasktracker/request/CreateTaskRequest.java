package ru.ertegix.ates.tasktracker.request;

import lombok.Getter;

@Getter
public class CreateTaskRequest {

    private String jiraId;
    private String description;
}
