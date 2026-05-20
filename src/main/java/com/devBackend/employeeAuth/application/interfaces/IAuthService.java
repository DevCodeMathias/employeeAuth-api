package com.devBackend.employeeAuth.application.interfaces;

import com.devBackend.employeeAuth.application.dto.LoginRequest;
import com.devBackend.employeeAuth.application.dto.TokenResponse;

public interface IAuthService {

    TokenResponse login(LoginRequest request);
}
