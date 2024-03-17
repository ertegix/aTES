package ru.ertegix.ates.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ertegix.ates.common.Role;

import javax.persistence.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "USER")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID publicId;
    private String username;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    private int balance;

    public User(UUID publicId, String username, Role role) {
        this.publicId = publicId;
        this.username = username;
        this.role = role;
    }
}
