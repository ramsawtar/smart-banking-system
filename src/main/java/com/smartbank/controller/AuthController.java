package com.smartbank.controller;

import com.smartbank.dto.LoginRequest;
import com.smartbank.dto.LoginResponse;
import com.smartbank.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<com.smartbank.dto.UserResponse> register(
            @Valid @RequestBody com.smartbank.dto.UserRequest request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }
}