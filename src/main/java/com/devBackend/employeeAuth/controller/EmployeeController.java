package com.devBackend.employeeAuth.controller;

import com.devBackend.employeeAuth.application.dto.EmployeeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devBackend.employeeAuth.application.interfaces.IEmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final IEmployeeService employeeService;

    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@Valid @RequestBody EmployeeRequest request) {
        employeeService.create(request);
        return "usuario criado com sucesso";
    }

    @GetMapping("/healthcheck")
    public String healthCheck() {
        return "OK";
    }

}
