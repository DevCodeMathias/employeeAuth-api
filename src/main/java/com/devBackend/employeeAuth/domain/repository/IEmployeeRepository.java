package com.devBackend.employeeAuth.domain.repository;

import java.util.Optional;

import com.devBackend.employeeAuth.domain.entity.Employees;
import com.devBackend.employeeAuth.infrastructure.model.Envelope;

public interface IEmployeeRepository {

    Optional<Envelope<Employees>> findByBodyCpf(String cpf);

    boolean existsByCpf(String cpf);

    Envelope<Employees> save(Employees employee);
}
