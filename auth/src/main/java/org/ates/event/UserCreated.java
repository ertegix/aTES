package org.ates.event;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserCreated {

    private final String id;
    private final String username;
    private final String role;

}
