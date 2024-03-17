package ru.ertegix.ates.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private UUID userPublicId;
    private Long balance;

    public Account(UUID userPublicId, Long balance) {
        this.userPublicId = userPublicId;
        this.balance = balance;
    }

    public void addToBalance(Long value) {
        this.balance = this.balance + value;
    }
}
