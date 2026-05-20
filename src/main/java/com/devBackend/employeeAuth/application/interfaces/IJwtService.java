package com.devBackend.employAuth.application.interfaces;

import com.devBackend.employAuth.domain.entity.Employees;
import com.devBackend.employAuth.infrastructure.model.Envelope;

public interface IJwtService {

    String generateToken(Envelope<Employees> employeeEnvelope);

    long expiresInSeconds();
}
