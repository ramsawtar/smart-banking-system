package com.smartbank.service;

import com.smartbank.dto.BeneficiaryRequest;
import com.smartbank.dto.BeneficiaryResponse;
import com.smartbank.entity.Beneficiary;
import com.smartbank.entity.User;
import com.smartbank.exception.ResourceNotFoundException;
import com.smartbank.repository.BeneficiaryRepository;
import com.smartbank.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository,
            UserRepository userRepository,
            UserService userService) {

        this.beneficiaryRepository = beneficiaryRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    private BeneficiaryResponse toResponse(Beneficiary b) {
        return new BeneficiaryResponse(
                b.getId(),
                b.getName(),
                b.getAccountNumber(),
                b.getBankName(),
                b.getUser() != null ? b.getUser().getId() : null
        );
    }

    public BeneficiaryResponse addBeneficiary(Long userId, BeneficiaryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (beneficiaryRepository.existsByUserIdAndAccountNumber(userId, request.getAccountNumber())) {
            throw new IllegalArgumentException("Beneficiary with account number " + request.getAccountNumber() + " already exists");
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setName(request.getName());
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setBankName(request.getBankName() != null && !request.getBankName().trim().isEmpty() ? request.getBankName() : "Smart Bank");
        beneficiary.setUser(user);

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return toResponse(saved);
    }

    public BeneficiaryResponse addBeneficiaryForCurrentUser(BeneficiaryRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        return addBeneficiary(currentUser.getId(), request);
    }

    public List<BeneficiaryResponse> getBeneficiariesByUser(Long userId) {
        return beneficiaryRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BeneficiaryResponse> getMyBeneficiaries() {
        User currentUser = userService.getCurrentUserEntity();
        return beneficiaryRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BeneficiaryResponse getBeneficiaryById(Long id) {
        Beneficiary b = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + id));
        return toResponse(b);
    }

    public void deleteBeneficiary(Long id) {
        if (!beneficiaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Beneficiary not found with id: " + id);
        }
        beneficiaryRepository.deleteById(id);
    }
}