package com.devBackend.employeeAuth.domain.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApplicationException {

    public InvalidCredentialsException() {
        super("Invalid CPF or password", HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
    }
}
