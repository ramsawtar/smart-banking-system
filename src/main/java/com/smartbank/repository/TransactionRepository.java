package com.smartbank.repository;

import com.smartbank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);

    List<Transaction> findByAccountUserIdOrderByTransactionDateDesc(Long userId);

    List<Transaction> findAllByOrderByTransactionDateDesc();

    List<Transaction> findTop10ByOrderByTransactionDateDesc();

    List<Transaction> findTop5ByAccountUserIdOrderByTransactionDateDesc(Long userId);

    long countByTransactionType(String transactionType);
}