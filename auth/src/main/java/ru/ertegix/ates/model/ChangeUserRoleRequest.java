package ru.ertegix.ates.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ChangeUserRoleRequest {

    private UUID publicId;
    private Role role;
}
