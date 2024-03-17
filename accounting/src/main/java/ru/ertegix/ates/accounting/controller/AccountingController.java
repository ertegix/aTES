package ru.ertegix.ates.accounting.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ertegix.ates.accounting.dto.BillingCycleDto;
import ru.ertegix.ates.accounting.dto.TransactionDto;
import ru.ertegix.ates.accounting.model.BillingCycle;
import ru.ertegix.ates.accounting.model.Task;
import ru.ertegix.ates.accounting.model.Transaction;
import ru.ertegix.ates.accounting.model.User;
import ru.ertegix.ates.accounting.repo.BillingCycleRepository;
import ru.ertegix.ates.accounting.repo.TaskRepository;
import ru.ertegix.ates.accounting.repo.TransactionRepository;
import ru.ertegix.ates.accounting.repo.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/accounting", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountingController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BillingCycleRepository billingCycleRepository;

    @GetMapping("/allTasks")
    public @ResponseBody List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/allUsers")
    public @ResponseBody List<User> getAllUsers() {
        return userRepository.findAll();
    }

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
    public Integer getMoneyMoneyMoney() {
        List<Task> tasksAssignedToday = taskRepository.findAssignedNotCompletedToday();
        List<Task> tasksCompletedToday = taskRepository.findCompletedToday();

        Integer sumAssigned = tasksAssignedToday.stream()
                .map(Task::getAssignCost)
                .reduce(Integer::sum)
                .orElse(0);

        Integer sumCompleted = tasksCompletedToday.stream()
                .map(Task::getCompletionReward)
                .reduce(Integer::sum)
                .orElse(0);

        return sumAssigned - sumCompleted;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('WORKER')")
    @GetMapping("/allByUserId/{id}")
    public @ResponseBody List<TransactionDto> getTransactionByUser(@PathVariable String id) {
        return transactionRepository.findAllByUserPublicId(UUID.fromString(id))
                .stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/allByBillingCycle/{id}")
    public @ResponseBody List<Transaction> getTransactionsByBillingCycle(@PathVariable Long id) {
        return transactionRepository.findAllByBillingCycleId(id);
    }

}
