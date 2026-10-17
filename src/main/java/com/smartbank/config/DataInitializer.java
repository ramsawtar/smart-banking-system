package com.smartbank.config;

import com.smartbank.entity.Account;
import com.smartbank.entity.Beneficiary;
import com.smartbank.entity.Transaction;
import com.smartbank.entity.User;
import com.smartbank.repository.AccountRepository;
import com.smartbank.repository.BeneficiaryRepository;
import com.smartbank.repository.TransactionRepository;
import com.smartbank.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           BeneficiaryRepository beneficiaryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed Admin user
        User admin = userRepository.findByEmail("admin@smartbank.com")
                .or(() -> userRepository.findByPhone("9999999999"))
                .orElse(null);

        if (admin == null) {
            admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@smartbank.com");
            admin.setPhone("9999999999");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setStatus("ACTIVE");
            userRepository.save(admin);
        } else {
            admin.setEmail("admin@smartbank.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setStatus("ACTIVE");
            userRepository.save(admin);
        }

        // Seed Customer Priya
        User priya = userRepository.findByEmail("priya@smartbank.com")
                .or(() -> userRepository.findByPhone("9123456789"))
                .orElse(null);

        if (priya == null) {
            priya = new User();
            priya.setName("Priya Sharma");
            priya.setEmail("priya@smartbank.com");
            priya.setPhone("9123456789");
            priya.setPassword(passwordEncoder.encode("priya123"));
            priya.setRole("CUSTOMER");
            priya.setStatus("ACTIVE");
            priya = userRepository.save(priya);
        } else {
            priya.setEmail("priya@smartbank.com");
            priya.setPassword(passwordEncoder.encode("priya123"));
            priya.setStatus("ACTIVE");
            userRepository.save(priya);
        }

        Account priyaAccount = accountRepository.findByAccountNumber("1000987654").orElse(null);
        if (priyaAccount == null) {
            priyaAccount = new Account();
            priyaAccount.setAccountNumber("1000987654");
            priyaAccount.setAccountType("SAVINGS");
            priyaAccount.setBalance(new BigDecimal("25000.00"));
            priyaAccount.setStatus("ACTIVE");
            priyaAccount.setUser(priya);
            accountRepository.save(priyaAccount);
        }

        // Seed Customer Ram
        User ram = userRepository.findByEmail("ram@smartbank.com")
                .or(() -> userRepository.findByPhone("9876543210"))
                .orElse(null);

        if (ram == null) {
            ram = new User();
            ram.setName("Ram");
            ram.setEmail("ram@smartbank.com");
            ram.setPhone("9876543210");
            ram.setPassword(passwordEncoder.encode("ram123"));
            ram.setRole("CUSTOMER");
            ram.setStatus("ACTIVE");
            ram = userRepository.save(ram);
        } else {
            ram.setEmail("ram@smartbank.com");
            ram.setPassword(passwordEncoder.encode("ram123"));
            ram.setStatus("ACTIVE");
            userRepository.save(ram);
        }

        // Create Ram's account with ₹50,000 balance if not exists
        Account ramAccount = accountRepository.findByAccountNumber("1000123456").orElse(null);
        if (ramAccount == null) {
            ramAccount = new Account();
            ramAccount.setAccountNumber("1000123456");
            ramAccount.setAccountType("SAVINGS");
            ramAccount.setBalance(new BigDecimal("50000.00"));
            ramAccount.setStatus("ACTIVE");
            ramAccount.setUser(ram);
            ramAccount = accountRepository.save(ramAccount);
        }

        // Add sample transactions for Ram's account if empty
        if (transactionRepository.findByAccountId(ramAccount.getId()).isEmpty()) {
            Transaction tx1 = new Transaction();
            tx1.setTransactionType("DEPOSIT");
            tx1.setAmount(new BigDecimal("10000.00"));
            tx1.setBalanceAfter(new BigDecimal("52500.00"));
            tx1.setDescription("Salary");
            tx1.setAccount(ramAccount);
            tx1.setTransactionDate(LocalDateTime.now().minusDays(3));
            transactionRepository.save(tx1);

            Transaction tx2 = new Transaction();
            tx2.setTransactionType("WITHDRAW");
            tx2.setAmount(new BigDecimal("2000.00"));
            tx2.setBalanceAfter(new BigDecimal("50500.00"));
            tx2.setDescription("Shopping");
            tx2.setAccount(ramAccount);
            tx2.setTransactionDate(LocalDateTime.now().minusDays(2));
            transactionRepository.save(tx2);

            Transaction tx3 = new Transaction();
            tx3.setTransactionType("WITHDRAW");
            tx3.setAmount(new BigDecimal("500.00"));
            tx3.setBalanceAfter(new BigDecimal("50000.00"));
            tx3.setDescription("Food");
            tx3.setAccount(ramAccount);
            tx3.setTransactionDate(LocalDateTime.now().minusDays(1));
            transactionRepository.save(tx3);
        }

        // Add Beneficiary for Ram if not present
        if (!beneficiaryRepository.existsByUserIdAndAccountNumber(ram.getId(), "1000987654")) {
            Beneficiary beneficiary = new Beneficiary();
            beneficiary.setName("Priya Sharma");
            beneficiary.setAccountNumber("1000987654");
            beneficiary.setBankName("Smart Bank");
            beneficiary.setUser(ram);
            beneficiaryRepository.save(beneficiary);
        }
    }
}
