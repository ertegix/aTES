package ru.ertegix.ates.accounting.dto;

import lombok.Getter;
import lombok.Setter;
import ru.ertegix.ates.accounting.model.Account;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AccountInfo {

    private final UUID userPublicId;
    private final Long balance;
    final List<TransactionDto> transactions;


    public AccountInfo(Account account, List<TransactionDto> transactions) {
        this.userPublicId = account.getUserPublicId();
        this.balance = account.getBalance();
        this.transactions = transactions;
    }
}
