package ru.ertegix.ates.accounting.model;

import java.math.BigDecimal;

public class Account {

    private final BigDecimal balance;

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
