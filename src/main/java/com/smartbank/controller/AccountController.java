package com.smartbank.controller;

import com.smartbank.dto.AccountRequest;
import com.smartbank.dto.AccountResponse;
import com.smartbank.dto.MoneyRequest;
import com.smartbank.dto.TransferRequest;
import com.smartbank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create account for currently authenticated user
    @PostMapping
    public ResponseEntity<AccountResponse> createAccountForCurrentUser(
            @Valid @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.createAccountForCurrentUser(request));
    }

    // Create account for specific user
    @PostMapping("/user/{userId}")
    public ResponseEntity<AccountResponse> createAccount(
            @PathVariable Long userId,
            @Valid @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.createAccount(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/my")
    public ResponseEntity<List<AccountResponse>> getMyAccounts() {
        return ResponseEntity.ok(accountService.getMyAccounts());
    }

    @GetMapping("/my/{accountId}")
    public ResponseEntity<AccountResponse> getMyAccount(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getMyAccount(accountId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted successfully");
    }

    // Deposit via path parameter
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountResponse> depositByPath(
            @PathVariable Long accountId,
            @Valid @RequestBody MoneyRequest request) {
        return ResponseEntity.ok(accountService.deposit(accountId, request));
    }

    // Deposit via request body
    @PostMapping("/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @Valid @RequestBody MoneyRequest request) {
        if (request.getAccountId() == null) {
            throw new IllegalArgumentException("Account ID is required for deposit");
        }
        return ResponseEntity.ok(accountService.deposit(request.getAccountId(), request));
    }

    // Withdraw via path parameter
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<AccountResponse> withdrawByPath(
            @PathVariable Long accountId,
            @Valid @RequestBody MoneyRequest request) {
        return ResponseEntity.ok(accountService.withdraw(accountId, request));
    }

    // Withdraw via request body
    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @Valid @RequestBody MoneyRequest request) {
        if (request.getAccountId() == null) {
            throw new IllegalArgumentException("Account ID is required for withdrawal");
        }
        return ResponseEntity.ok(accountService.withdraw(request.getAccountId(), request));
    }

    // Transfer via path parameter
    @PostMapping("/{senderAccountId}/transfer")
    public ResponseEntity<String> transferByPath(
            @PathVariable Long senderAccountId,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(accountService.transfer(senderAccountId, request));
    }

    // Transfer via request body
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @Valid @RequestBody TransferRequest request) {
        if (request.getSenderAccountId() == null) {
            throw new IllegalArgumentException("Sender Account ID is required for transfer");
        }
        return ResponseEntity.ok(accountService.transfer(request.getSenderAccountId(), request));
    }
}