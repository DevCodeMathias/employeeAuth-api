package com.devBackend.employeeAuth.infrastructure.repository;

import java.util.Optional;

import com.devBackend.employeeAuth.domain.entity.Employees;
import com.devBackend.employeeAuth.domain.repository.IEmployeeRepository;
import com.devBackend.employeeAuth.infrastructure.model.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class MongoEmployeeRepository implements IEmployeeRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoEmployeeRepository.class);

    private final SpringDataEmployeeMongoRepository mongoRepository;

    public MongoEmployeeRepository(SpringDataEmployeeMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Envelope<Employees> save(Employees employee) {
        log.debug("Saving employee document for cpf={}", maskCpf(employee.cpf));
        Envelope<Employees> savedEmployee = mongoRepository.save(Envelope.of(employee));
        log.debug("Employee document saved id={}", savedEmployee.getId());
        return savedEmployee;
    }

    @Override
    public Optional<Envelope<Employees>> findByBodyCpf(String cpf) {
        log.debug("Finding employee document by cpf={}", maskCpf(cpf));
        Optional<Envelope<Employees>> employee = mongoRepository.findByBodyCpf(cpf);
        log.debug("Employee document lookup cpf={} found={}", maskCpf(cpf), employee.isPresent());
        return employee;
    }

    @Override
    public boolean existsByCpf(String cpf) {
        log.debug("Checking employee document existence by cpf={}", maskCpf(cpf));
        boolean exists = mongoRepository.existsByCpf(cpf);
        log.debug("Employee document existence cpf={} exists={}", maskCpf(cpf), exists);
        return exists;
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }

        return "***" + cpf.substring(cpf.length() - 4);
    }
}
