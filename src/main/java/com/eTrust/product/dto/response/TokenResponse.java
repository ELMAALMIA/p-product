package com.eTrust.product.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String type,
        long expiresIn
) {
    public TokenResponse(String accessToken, String refreshToken, long expiresIn) {
        this(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
