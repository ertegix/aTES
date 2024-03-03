package org.ates.tasktracker.event.incoming_events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCreated {

    private final String id;
    private final String username;
    private final String role;

}
