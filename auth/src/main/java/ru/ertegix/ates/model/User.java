package ru.ertegix.ates.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
    private Long id;
    private UUID publicId;
    private String username;
    private String fullName;
    private String password;
    private String role;

}