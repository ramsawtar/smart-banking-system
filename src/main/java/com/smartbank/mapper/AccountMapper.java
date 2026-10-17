package com.smartbank.mapper;

import com.smartbank.dto.AccountRequest;
import com.smartbank.dto.AccountResponse;
import com.smartbank.entity.Account;

public class AccountMapper {

    private AccountMapper() {
    }

    public static AccountResponse toResponse(Account account) {
        Long userId = account.getUser() != null ? account.getUser().getId() : null;
        String userName = account.getUser() != null ? account.getUser().getName() : null;

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus(),
                userId,
                userName
        );
    }

    public static Account toEntity(AccountRequest request) {

        Account account = new Account();

        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());

        return account;
    }
}