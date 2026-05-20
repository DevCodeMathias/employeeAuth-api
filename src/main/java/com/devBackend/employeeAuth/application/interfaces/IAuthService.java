package com.devBackend.employAuth.application.interfaces;

import com.devBackend.employAuth.application.dto.LoginRequest;
import com.devBackend.employAuth.application.dto.TokenResponse;

public interface IAuthService {

    TokenResponse login(LoginRequest request);
}
