package com.eTrust.product.service;

import com.eTrust.product.dto.request.LoginRequest;
import com.eTrust.product.dto.request.RefreshTokenRequest;
import com.eTrust.product.dto.response.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(RefreshTokenRequest request);
    void logout(String refreshToken);
}
