package com.devBackend.employeeAuth.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.devBackend.employeeAuth.domain.entity.Employees;
import com.devBackend.employeeAuth.infrastructure.model.Envelope;

public interface EmployeeRepository extends MongoRepository<Envelope<Employees>, String> {

    @Query("{'body.cpf': ?0}")
    Optional<Envelope<Employees>> findByBodyCpf(String cpf);

    @Query(value = "{'body.cpf': ?0}", exists = true)
    boolean existsByCpf(String cpf);
}
