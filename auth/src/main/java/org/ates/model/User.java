package org.ates.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AUTH_USER")
public class User {

    @Id
    private String id;

    private String username;
    private String fullName;
    private String password;

}
