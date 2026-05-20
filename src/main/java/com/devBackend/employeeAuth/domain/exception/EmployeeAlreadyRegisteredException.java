package com.devBackend.employAuth.domain.exception;

import org.springframework.http.HttpStatus;

public class EmployeeAlreadyRegisteredException extends ApplicationException {

    public EmployeeAlreadyRegisteredException() {
        super("Employee CPF already registered", HttpStatus.CONFLICT, "EMPLOYEE_ALREADY_REGISTERED");
    }
}
