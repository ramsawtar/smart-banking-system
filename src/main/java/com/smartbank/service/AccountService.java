package com.smartbank.service;

import com.smartbank.dto.AccountRequest;
import com.smartbank.dto.AccountResponse;
import com.smartbank.dto.MoneyRequest;
import com.smartbank.dto.TransferRequest;
import com.smartbank.entity.Account;
import com.smartbank.entity.Transaction;
import com.smartbank.entity.User;
import com.smartbank.exception.AccountStatusException;
import com.smartbank.exception.InsufficientFundsException;
import com.smartbank.exception.ResourceNotFoundException;
import com.smartbank.mapper.AccountMapper;
import com.smartbank.repository.AccountRepository;
import com.smartbank.repository.TransactionRepository;
import com.smartbank.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final Random random = new SecureRandom();

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    private String generateUniqueAccountNumber() {
        String accNum;
        do {
            accNum = "1000" + String.format("%06d", random.nextInt(1_000_000));
        } while (accountRepository.existsByAccountNumber(accNum));
        return accNum;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public AccountResponse createAccount(Long userId, AccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Account account = AccountMapper.toEntity(request);
        account.setUser(user);

        if (account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
            account.setAccountNumber(generateUniqueAccountNumber());
        } else {
            if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
                throw new IllegalArgumentException("Account number already exists: " + account.getAccountNumber());
            }
        }

        BigDecimal initialDeposit = request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO;
        if (initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }

        account.setBalance(initialDeposit);
        account.setStatus("ACTIVE");

        Account savedAccount = accountRepository.save(account);

        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            Transaction initialTx = new Transaction();
            initialTx.setTransactionType("DEPOSIT");
            initialTx.setAmount(initialDeposit);
            initialTx.setBalanceAfter(initialDeposit);
            initialTx.setDescription("Initial Account Opening Deposit");
            initialTx.setAccount(savedAccount);
            transactionRepository.save(initialTx);
        }

        return AccountMapper.toResponse(savedAccount);
    }

    @Transactional
    public AccountResponse createAccountForCurrentUser(AccountRequest request) {
        User currentUser = getAuthenticatedUser();
        return createAccount(currentUser.getId(), request);
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    public AccountResponse getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return AccountMapper.toResponse(account);
    }

    public Account getAccountEntity(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    public List<AccountResponse> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    public List<AccountResponse> getMyAccounts() {
        User currentUser = getAuthenticatedUser();
        return accountRepository.findByUserId(currentUser.getId())
                .stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    public AccountResponse getMyAccount(Long accountId) {
        User currentUser = getAuthenticatedUser();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        if (!isAdmin && !account.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to access this account");
        }

        return AccountMapper.toResponse(account);
    }

    public AccountResponse updateAccountStatus(Long accountId, String status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        String upperStatus = status.trim().toUpperCase();
        if (!"ACTIVE".equals(upperStatus) && !"BLOCKED".equals(upperStatus)) {
            throw new IllegalArgumentException("Status must be either ACTIVE or BLOCKED");
        }

        account.setStatus(upperStatus);
        Account saved = accountRepository.save(account);
        return AccountMapper.toResponse(saved);
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    @Transactional
    public AccountResponse deposit(Long accountId, MoneyRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        checkAccountStatus(account);

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription() != null && !request.getDescription().trim().isEmpty()
                ? request.getDescription() : "Cash Deposit");
        transaction.setAccount(account);
        transactionRepository.save(transaction);

        return AccountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse withdraw(Long accountId, MoneyRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        checkAccountStatus(account);

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than zero");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Available balance: " + account.getBalance());
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("WITHDRAW");
        transaction.setAmount(amount);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription() != null && !request.getDescription().trim().isEmpty()
                ? request.getDescription() : "Cash Withdrawal");
        transaction.setAccount(account);
        transactionRepository.save(transaction);

        return AccountMapper.toResponse(account);
    }

    @Transactional
    public String transfer(Long senderAccountId, TransferRequest request) {
        Account sender = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender account not found with id: " + senderAccountId));

        Account receiver = accountRepository.findByAccountNumber(request.getReceiverAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver account not found: " + request.getReceiverAccountNumber()));

        checkAccountStatus(sender);
        checkAccountStatus(receiver);

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot transfer money to the same account");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Available balance: " + sender.getBalance());
        }

        BigDecimal senderNewBalance = sender.getBalance().subtract(amount);
        BigDecimal receiverNewBalance = receiver.getBalance().add(amount);

        sender.setBalance(senderNewBalance);
        receiver.setBalance(receiverNewBalance);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        String note = request.getDescription() != null && !request.getDescription().trim().isEmpty()
                ? request.getDescription() : "Transfer to " + receiver.getAccountNumber();

        // Sender record
        Transaction senderTransaction = new Transaction();
        senderTransaction.setTransactionType("TRANSFER_OUT");
        senderTransaction.setAmount(amount);
        senderTransaction.setBalanceAfter(senderNewBalance);
        senderTransaction.setDescription("Transfer to A/C " + receiver.getAccountNumber() + (note.isEmpty() ? "" : " - " + note));
        senderTransaction.setAccount(sender);
        transactionRepository.save(senderTransaction);

        // Receiver record
        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setTransactionType("TRANSFER_IN");
        receiverTransaction.setAmount(amount);
        receiverTransaction.setBalanceAfter(receiverNewBalance);
        receiverTransaction.setDescription("Received from A/C " + sender.getAccountNumber() + (note.isEmpty() ? "" : " - " + note));
        receiverTransaction.setAccount(receiver);
        transactionRepository.save(receiverTransaction);

        return "Money transferred successfully";
    }

    private void checkAccountStatus(Account account) {
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new AccountStatusException("Account " + account.getAccountNumber() + " is " + account.getStatus() + ". Operations not permitted.");
        }
    }
}