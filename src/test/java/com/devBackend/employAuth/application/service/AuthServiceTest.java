package com.devBackend.employAuth.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.devBackend.employAuth.application.dto.LoginRequest;
import com.devBackend.employAuth.application.interfaces.IJwtService;
import com.devBackend.employAuth.domain.entity.Employees;
import com.devBackend.employAuth.domain.exception.InvalidCredentialsException;
import com.devBackend.employAuth.infrastructure.model.Envelope;
import com.devBackend.employAuth.infrastructure.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IJwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest(" 52998224725 ", "password123");
        Envelope<Employees> employeeEnvelope = new Envelope<>("employee-id", null, null,
                new Employees("Maria", "52998224725", "encoded-password"));

        when(employeeRepository.findByBodyCpf("52998224725")).thenReturn(Optional.of(employeeEnvelope));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(employeeEnvelope)).thenReturn("access-token");

        var response = authService.login(request);

        assertEquals("access-token", response.accessToken());
        verify(employeeRepository).findByBodyCpf("52998224725");
        verify(jwtService).generateToken(employeeEnvelope);
    }

    @Test
    void loginThrowsInvalidCredentialsWhenCpfDoesNotExist() {
        LoginRequest request = new LoginRequest("52998224725", "password123");

        when(employeeRepository.findByBodyCpf("52998224725")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void loginThrowsInvalidCredentialsWhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest("52998224725", "wrong-password");
        Envelope<Employees> employeeEnvelope = new Envelope<>("employee-id", null, null,
                new Employees("Maria", "52998224725", "encoded-password"));

        when(employeeRepository.findByBodyCpf("52998224725")).thenReturn(Optional.of(employeeEnvelope));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verifyNoInteractions(jwtService);
    }
}
