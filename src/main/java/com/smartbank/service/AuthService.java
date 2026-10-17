package com.smartbank.service;

import com.smartbank.config.JwtService;
import com.smartbank.dto.LoginRequest;
import com.smartbank.dto.LoginResponse;
import com.smartbank.entity.User;
import com.smartbank.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.smartbank.config.CustomUserDetails;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService,
            UserService userService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public com.smartbank.dto.UserResponse register(com.smartbank.dto.UserRequest request) {
        return userService.createUser(request);
    }



    public LoginResponse login(LoginRequest request) {

        var authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token =
                jwtService.generateToken(userDetails);

        String role = userDetails.getUser().getRole();
        Long userId = userDetails.getUser().getId();

        return new LoginResponse(
                token,
                userId,
                userDetails.getUser().getName(),
                userDetails.getUser().getEmail(),
                role
        );
    }
}