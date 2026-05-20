package com.devBackend.employeeAuth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devBackend.employeeAuth.application.interfaces.IAuthService;
import com.devBackend.employeeAuth.application.dto.LoginRequest;
import com.devBackend.employeeAuth.application.dto.TokenResponse;
import com.devBackend.employeeAuth.infrastructure.model.Envelope;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        var token = authService.login(request);
        return token;
    }
}
