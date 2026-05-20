package com.devBackend.employeeAuth.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.devBackend.employeeAuth.application.dto.EmployeeRequest;
import com.devBackend.employeeAuth.domain.entity.Employees;
import com.devBackend.employeeAuth.domain.exception.EmployeeAlreadyRegisteredException;
import com.devBackend.employeeAuth.infrastructure.model.Envelope;
import com.devBackend.employeeAuth.infrastructure.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @SuppressWarnings("unchecked")
    void createSavesEmployeeWithNormalizedCpfAndEncodedPassword() {
        EmployeeRequest request = new EmployeeRequest("Maria", " 52998224725 ", "password123");

        when(employeeRepository.existsByCpf("52998224725")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(employeeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        employeeService.create(request);

        ArgumentCaptor<Envelope<Employees>> captor = ArgumentCaptor.forClass(Envelope.class);
        verify(employeeRepository).save(captor.capture());

        Employees employee = captor.getValue().body();
        assertEquals("Maria", employee.name);
        assertEquals("52998224725", employee.cpf);
        assertEquals("encoded-password", employee.password);
    }

    @Test
    void createThrowsWhenCpfIsAlreadyRegistered() {
        EmployeeRequest request = new EmployeeRequest("Maria", "52998224725", "password123");

        when(employeeRepository.existsByCpf("52998224725")).thenReturn(true);

        assertThrows(EmployeeAlreadyRegisteredException.class, () -> employeeService.create(request));
        verify(employeeRepository).existsByCpf("52998224725");
        verifyNoMoreInteractions(employeeRepository, passwordEncoder);
    }
}
