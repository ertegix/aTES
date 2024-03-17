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
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Account account;

    public User(UUID publicId, String username, Role role) {
        this.publicId = publicId;
        this.username = username;
        this.role = role;
        this.account = new Account(publicId, 0L);
    }

    public void addToBalance(Long value) {
        if (this.account != null) {
            this.account.addToBalance(value);
        }
    }
}
