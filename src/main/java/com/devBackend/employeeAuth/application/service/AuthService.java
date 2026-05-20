package com.devBackend.employAuth.application.service;

import com.devBackend.employAuth.application.dto.LoginRequest;
import com.devBackend.employAuth.application.dto.TokenResponse;
import com.devBackend.employAuth.application.interfaces.IAuthService;
import com.devBackend.employAuth.application.interfaces.IJwtService;
import com.devBackend.employAuth.domain.exception.InvalidCredentialsException;
import com.devBackend.employAuth.infrastructure.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;

    public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, IJwtService jwtService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        String normalizedCpf = request.cpf().trim();
        log.info("Login requested for cpf={}", maskCpf(normalizedCpf));

        var employeeEnvelope = employeeRepository.findByBodyCpf(normalizedCpf)
                .orElseThrow(() -> {
                    log.warn("Login failed: employee not found for cpf={}", maskCpf(normalizedCpf));
                    return new InvalidCredentialsException();
                });
        var employee = employeeEnvelope.body();

        if (!passwordEncoder.matches(request.password(), employee.password)) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(employeeEnvelope);
        log.info("Login succeeded for employeeId={}", employeeEnvelope.getId());

        return new TokenResponse(token);
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }

        return "***" + cpf.substring(cpf.length() - 4);
    }
}
