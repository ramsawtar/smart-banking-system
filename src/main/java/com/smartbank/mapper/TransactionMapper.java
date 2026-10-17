package com.smartbank.mapper;

import com.smartbank.dto.TransactionResponse;
import com.smartbank.entity.Transaction;

public class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionResponse toResponse(
            Transaction transaction) {

        Long accountId = transaction.getAccount() != null ? transaction.getAccount().getId() : null;
        String accountNumber = transaction.getAccount() != null ? transaction.getAccount().getAccountNumber() : null;

        return new TransactionResponse(
                transaction.getId(),
                accountId,
                accountNumber,
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getDescription(),
                transaction.getTransactionDate()
        );
    }
}