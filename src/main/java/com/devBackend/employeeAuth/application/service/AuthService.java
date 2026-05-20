package com.devBackend.employeeAuth.application.service;

import com.devBackend.employeeAuth.application.dto.LoginRequest;
import com.devBackend.employeeAuth.application.dto.TokenResponse;
import com.devBackend.employeeAuth.application.interfaces.IAuthService;
import com.devBackend.employeeAuth.application.interfaces.IJwtService;
import com.devBackend.employeeAuth.domain.exception.InvalidCredentialsException;
import com.devBackend.employeeAuth.domain.repository.IEmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final IEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;

    public AuthService(IEmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, IJwtService jwtService) {
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
