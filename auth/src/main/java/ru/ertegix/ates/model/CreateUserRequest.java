package ru.ertegix.ates.model;

import lombok.Data;

@Data
public class CreateUserRequest {

    private String username;
    private String password;
}
