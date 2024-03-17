package ru.ertegix.ates.model;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AUTH_USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID publicId;
    private String username;
    private String password;
    private String role;

    public User(UUID publicId, String username, String password, String role) {
        this.publicId = publicId;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
