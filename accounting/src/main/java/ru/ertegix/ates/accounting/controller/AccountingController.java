package ru.ertegix.ates.accounting.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import ru.ertegix.ates.accounting.dto.AccountInfo;
import ru.ertegix.ates.accounting.dto.BillingCycleDto;
import ru.ertegix.ates.accounting.dto.TransactionDto;
import ru.ertegix.ates.accounting.model.Account;
import ru.ertegix.ates.accounting.model.Task;
import ru.ertegix.ates.accounting.model.User;
import ru.ertegix.ates.accounting.repo.*;
import ru.ertegix.ates.accounting.security.JwtUser;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/accounting", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountingController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BillingCycleRepository billingCycleRepository;


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/allTransactions")
    public @ResponseBody List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/allBillingCycles")
    public @ResponseBody List<BillingCycleDto> getAllBIllingCycles() {
        return billingCycleRepository.findAll()
                .stream()
                .map(BillingCycleDto::new)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
    @GetMapping("/moneyMoneyMoney")
    public Long getMoneyMoneyMoney() {
        List<Task> tasksAssignedToday = taskRepository.findAssignedNotCompletedToday();
        List<Task> tasksCompletedToday = taskRepository.findCompletedToday();

        Long sumAssigned = tasksAssignedToday.stream()
                .map(Task::getAssignCost)
                .reduce(Long::sum)
                .orElse(0L);

        Long sumCompleted = tasksCompletedToday.stream()
                .map(Task::getCompletionReward)
                .reduce(Long::sum)
                .orElse(0L);

        return sumAssigned - sumCompleted;
    }

    @PreAuthorize("hasAuthority('WORKER')")
    @GetMapping("/myAccount")
    public @ResponseBody AccountInfo getTransactionForCurrentUser(Authentication auth) {
        Optional<Account> account = userPublicIdFromAuth(auth)
                .flatMap(accountRepository::findByUserPublicId);

        if (account.isPresent()) {

            AccountInfo accountInfo = new AccountInfo(
                    account.get(),
                    transactionRepository
                            .findAllByAccountIdAndCreateDate(
                                    account.get().getId(),
                                    LocalDate.now())
                            .stream()
                            .map(TransactionDto::new)
                            .collect(Collectors.toList())
            );
            return accountInfo;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Not account for user");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/forUser/{id}")
    public @ResponseBody AccountInfo getTransactionByUser(@PathVariable String id) {
        Optional<Account> account = accountRepository.findByUserPublicId(UUID.fromString(id));

        if (account.isPresent()) {
            return new AccountInfo(
                    account.get(),
                    transactionRepository
                            .findAllByAccountIdAndCreateDate(
                                    account.get().getId(),
                                    LocalDate.now())
                            .stream()
                            .map(TransactionDto::new)
                            .collect(Collectors.toList())
            );
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Not account found for user");
        }
    }

    private Optional<UUID> userPublicIdFromAuth(Authentication authentication) {
        UUID userPublicId = ((JwtUser) authentication.getPrincipal()).getPublicId();
        return Optional.ofNullable(userPublicId);
    }

}
