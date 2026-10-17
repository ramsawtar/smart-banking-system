package com.smartbank.controller;

import com.smartbank.dto.AccountResponse;
import com.smartbank.dto.AdminDashboardStats;
import com.smartbank.dto.StatusUpdateRequest;
import com.smartbank.dto.TransactionResponse;
import com.smartbank.dto.UserResponse;
import com.smartbank.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardStats> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<UserResponse>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(adminService.getAllAccounts());
    }

    @PutMapping("/accounts/{id}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateAccountStatus(id, request.getStatus()));
    }

    @PutMapping("/accounts/{id}/block")
    public ResponseEntity<AccountResponse> blockAccount(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.updateAccountStatus(id, "BLOCKED"));
    }

    @PutMapping("/accounts/{id}/unblock")
    public ResponseEntity<AccountResponse> unblockAccount(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.updateAccountStatus(id, "ACTIVE"));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }
}