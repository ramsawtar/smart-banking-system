package com.smartbank.service;

import com.smartbank.dto.TransactionResponse;
import com.smartbank.entity.Account;
import com.smartbank.entity.Transaction;
import com.smartbank.entity.User;
import com.smartbank.exception.ResourceNotFoundException;
import com.smartbank.mapper.TransactionMapper;
import com.smartbank.repository.AccountRepository;
import com.smartbank.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              UserService userService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public Transaction createTransaction(
            Long accountId,
            Transaction transaction) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        transaction.setAccount(account);
        return transactionRepository.save(transaction);
    }

    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }

        return transactionRepository
                .findByAccountIdOrderByTransactionDateDesc(accountId)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    public List<TransactionResponse> getMyTransactions() {
        User currentUser = userService.getCurrentUserEntity();
        return transactionRepository
                .findByAccountUserIdOrderByTransactionDateDesc(currentUser.getId())
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    public List<TransactionResponse> getMyRecentTransactions() {
        User currentUser = userService.getCurrentUserEntity();
        return transactionRepository
                .findTop10ByOrderByTransactionDateDesc()
                .stream()
                .filter(tx -> tx.getAccount() != null && tx.getAccount().getUser() != null && tx.getAccount().getUser().getId().equals(currentUser.getId()))
                .map(TransactionMapper::toResponse)
                .toList();
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository
                .findAllByOrderByTransactionDateDesc()
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return TransactionMapper.toResponse(tx);
    }
}