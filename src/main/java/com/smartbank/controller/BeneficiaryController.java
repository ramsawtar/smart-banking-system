package com.smartbank.controller;

import com.smartbank.dto.BeneficiaryRequest;
import com.smartbank.dto.BeneficiaryResponse;
import com.smartbank.service.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    // Add beneficiary for current authenticated user
    @PostMapping
    public ResponseEntity<BeneficiaryResponse> addBeneficiaryForCurrentUser(
            @Valid @RequestBody BeneficiaryRequest request) {
        return ResponseEntity.ok(beneficiaryService.addBeneficiaryForCurrentUser(request));
    }

    // Add beneficiary for specific user
    @PostMapping("/user/{userId}")
    public ResponseEntity<BeneficiaryResponse> addBeneficiary(
            @PathVariable Long userId,
            @Valid @RequestBody BeneficiaryRequest request) {
        return ResponseEntity.ok(beneficiaryService.addBeneficiary(userId, request));
    }

    // Get beneficiaries for current authenticated user
    @GetMapping("/my")
    public ResponseEntity<List<BeneficiaryResponse>> getMyBeneficiaries() {
        return ResponseEntity.ok(beneficiaryService.getMyBeneficiaries());
    }

    // Get beneficiaries for specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BeneficiaryResponse>> getBeneficiaries(
            @PathVariable Long userId) {
        return ResponseEntity.ok(beneficiaryService.getBeneficiariesByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> getBeneficiary(
            @PathVariable Long id) {
        return ResponseEntity.ok(beneficiaryService.getBeneficiaryById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBeneficiary(
            @PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.ok("Beneficiary deleted successfully");
    }
}