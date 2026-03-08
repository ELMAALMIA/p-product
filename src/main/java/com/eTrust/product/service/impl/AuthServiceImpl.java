package com.eTrust.product.service.impl;

import com.eTrust.product.dto.request.LoginRequest;
import com.eTrust.product.dto.request.RefreshTokenRequest;
import com.eTrust.product.dto.response.TokenResponse;
import com.eTrust.product.entity.RefreshTokenEntity;
import com.eTrust.product.repository.RefreshTokenRepository;
import com.eTrust.product.security.JwtService;
import com.eTrust.product.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthServiceImpl(AuthenticationManager authManager,
                           JwtService jwtService,
                           UserDetailsService userDetailsService,
                           RefreshTokenRepository refreshTokenRepository) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        String accessToken = jwtService.generateToken(user);
        String refreshToken = createRefreshToken(user.getUsername());
        return new TokenResponse(accessToken, refreshToken, jwtService.getExpirationTime());
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        RefreshTokenEntity stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (stored.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new BadCredentialsException("Refresh token expired");
        }

        refreshTokenRepository.delete(stored);

        UserDetails user = userDetailsService.loadUserByUsername(stored.getUsername());
        String accessToken = jwtService.generateToken(user);
        String newRefreshToken = createRefreshToken(stored.getUsername());
        return new TokenResponse(accessToken, newRefreshToken, jwtService.getExpirationTime());
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    private String createRefreshToken(String username) {
        refreshTokenRepository.deleteByUsername(username);

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setToken(UUID.randomUUID().toString());
        entity.setUsername(username);
        entity.setExpiryDate(Instant.now().plusMillis(jwtService.getRefreshExpirationTime()));
        refreshTokenRepository.save(entity);
        return entity.getToken();
    }
}
