package com.devBackend.employeeAuth.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.devBackend.employeeAuth.domain.entity.Employees;
import com.devBackend.employeeAuth.infrastructure.model.Envelope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MongoEmployeeRepositoryTest {

    @Mock
    private SpringDataEmployeeMongoRepository springDataRepository;

    @InjectMocks
    private MongoEmployeeRepository employeeRepository;

    @Test
    @SuppressWarnings("unchecked")
    void saveWrapsEmployeeInEnvelopeAndDelegatesToSpringDataRepository() {
        Employees employee = new Employees("Maria", "52998224725", "encoded-password");
        Envelope<Employees> savedEnvelope = new Envelope<>("employee-id", null, null, employee);

        when(springDataRepository.save(any())).thenReturn(savedEnvelope);

        Envelope<Employees> result = employeeRepository.save(employee);

        ArgumentCaptor<Envelope<Employees>> captor = ArgumentCaptor.forClass(Envelope.class);
        verify(springDataRepository).save(captor.capture());

        assertSame(savedEnvelope, result);
        assertSame(employee, captor.getValue().body());
    }

    @Test
    void findByBodyCpfDelegatesToSpringDataRepository() {
        Envelope<Employees> employeeEnvelope = new Envelope<>("employee-id", null, null,
                new Employees("Maria", "52998224725", "encoded-password"));

        when(springDataRepository.findByBodyCpf("52998224725")).thenReturn(Optional.of(employeeEnvelope));

        Optional<Envelope<Employees>> result = employeeRepository.findByBodyCpf("52998224725");

        assertTrue(result.isPresent());
        assertSame(employeeEnvelope, result.get());
        verify(springDataRepository).findByBodyCpf("52998224725");
    }

    @Test
    void existsByCpfDelegatesToSpringDataRepository() {
        when(springDataRepository.existsByCpf("52998224725")).thenReturn(true);

        boolean result = employeeRepository.existsByCpf("52998224725");

        assertEquals(true, result);
        verify(springDataRepository).existsByCpf("52998224725");
    }
}
