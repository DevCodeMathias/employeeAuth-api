package com.devBackend.employeeAuth.application.dto;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "cpf is required")
        @CPF(message = "cpf must be a valid CPF document")
        String cpf,
        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must have at least 8 characters")
        String password

) {
}
