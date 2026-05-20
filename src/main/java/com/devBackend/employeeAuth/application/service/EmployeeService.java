package com.devBackend.employeeAuth.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devBackend.employeeAuth.domain.entity.Employees;
import com.devBackend.employeeAuth.application.interfaces.IEmployeeService;
import com.devBackend.employeeAuth.application.dto.EmployeeRequest;
import com.devBackend.employeeAuth.domain.exception.EmployeeAlreadyRegisteredException;
import com.devBackend.employeeAuth.infrastructure.model.Envelope;
import com.devBackend.employeeAuth.infrastructure.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmployeeService implements IEmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void create(EmployeeRequest request) {
        String normalizedCpf = request.cpf().trim();
        log.info("Employee registration requested for cpf={}", maskCpf(normalizedCpf));

        if (employeeRepository.existsByCpf(normalizedCpf)) {
            log.warn("Employee registration failed: cpf already registered cpf={}", maskCpf(normalizedCpf));
            throw new EmployeeAlreadyRegisteredException();
        }

        Employees employee = new Employees(
                request.name(),
                normalizedCpf,
                passwordEncoder.encode(request.password())
        );

        Envelope<Employees> savedEmployee = employeeRepository.save(Envelope.of(employee));
        log.info("Employee registered successfully employeeId={}", savedEmployee.getId());
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }

        return "***" + cpf.substring(cpf.length() - 4);
    }
}
