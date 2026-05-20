package com.devBackend.employeeAuth.application.dto;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "cpf is required")
        @CPF(message = "cpf must be a valid CPF document")
        String cpf,
        @NotBlank(message = "password is required")
        String password
) {
}
