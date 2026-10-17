package com.smartbank.service;

import com.smartbank.dto.AccountResponse;
import com.smartbank.dto.AdminDashboardStats;
import com.smartbank.dto.TransactionResponse;
import com.smartbank.dto.UserResponse;
import com.smartbank.mapper.AccountMapper;
import com.smartbank.mapper.TransactionMapper;
import com.smartbank.mapper.UserMapper;
import com.smartbank.repository.AccountRepository;
import com.smartbank.repository.TransactionRepository;
import com.smartbank.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public AdminService(UserRepository userRepository,
                        AccountRepository accountRepository,
                        TransactionRepository transactionRepository,
                        AccountService accountService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public AdminDashboardStats getDashboardStats() {
        long totalCustomers = userRepository.findAll().stream()
                .filter(u -> "CUSTOMER".equalsIgnoreCase(u.getRole()))
                .count();

        long totalAccounts = accountRepository.count();
        long activeAccounts = accountRepository.countByStatus("ACTIVE");
        long blockedAccounts = accountRepository.countByStatus("BLOCKED");

        BigDecimal totalBankBalance = accountRepository.getTotalBankBalance();
        if (totalBankBalance == null) {
            totalBankBalance = BigDecimal.ZERO;
        }

        long totalTransactions = transactionRepository.count();
        long totalDeposits = transactionRepository.countByTransactionType("DEPOSIT");
        long totalWithdrawals = transactionRepository.countByTransactionType("WITHDRAW");
        long totalTransfers = transactionRepository.countByTransactionType("TRANSFER_OUT");

        List<TransactionResponse> recentTransactions = transactionRepository
                .findTop10ByOrderByTransactionDateDesc()
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();

        return new AdminDashboardStats(
                totalCustomers,
                totalAccounts,
                activeAccounts,
                blockedAccounts,
                totalBankBalance,
                totalTransactions,
                totalDeposits,
                totalWithdrawals,
                totalTransfers,
                recentTransactions
        );
    }

    public List<UserResponse> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(u -> "CUSTOMER".equalsIgnoreCase(u.getRole()))
                .map(UserMapper::toResponse)
                .toList();
    }

    public List<AccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    public AccountResponse updateAccountStatus(Long accountId, String status) {
        return accountService.updateAccountStatus(accountId, status);
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAllByOrderByTransactionDateDesc()
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }
}
