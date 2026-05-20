package com.devBackend.employeeAuth.application.interfaces;

import com.devBackend.employeeAuth.domain.entity.Employees;
import com.devBackend.employeeAuth.infrastructure.model.Envelope;

public interface IJwtService {

    String generateToken(Envelope<Employees> employeeEnvelope);

    long expiresInSeconds();
}
