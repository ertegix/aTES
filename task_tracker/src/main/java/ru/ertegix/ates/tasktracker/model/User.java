package ru.ertegix.ates.tasktracker.model;

import lombok.*;
import ru.ertegix.ates.common.Role;

import javax.persistence.*;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID publicId;
    private String username;
    @Enumerated(value = EnumType.STRING)
    private Role role;

    public User(UUID publicId, String username, Role role) {
        this.publicId = publicId;
        this.username = username;
        this.role = role;
    }
}
