/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.library.api.dto.LoginRequest;
import ru.mentee.library.api.dto.LoginResponse;
import ru.mentee.library.domain.model.RefreshToken;
import ru.mentee.library.domain.model.User;
import ru.mentee.library.domain.repository.RefreshTokenRepository;
import ru.mentee.library.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String accessToken = tokenService.generateAccessToken(userDetails);
        String refreshTokenValue = tokenService.generateRefreshToken(userDetails);

        User user =
                userRepository
                        .findByEmail(request.getUsername())
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "User not found: " + request.getUsername()));

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(refreshTokenValue)
                        .userId(user.getId())
                        .expiresAt(
                                Instant.now().plusSeconds(tokenService.getRefreshTokenExpiration()))
                        .createdAt(Instant.now())
                        .build();

        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(accessToken, refreshTokenValue);
    }

    @Transactional
    public LoginResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(refreshTokenValue)
                        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!tokenService.isTokenValid(refreshTokenValue)
                || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        tokenService.getUsernameFromToken(refreshTokenValue));

        String newAccessToken = tokenService.generateAccessToken(userDetails);
        String newRefreshTokenValue = tokenService.generateRefreshToken(userDetails);

        refreshTokenRepository.delete(refreshToken);

        User user =
                userRepository
                        .findByEmail(tokenService.getUsernameFromToken(refreshTokenValue))
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "User not found: "
                                                        + tokenService.getUsernameFromToken(
                                                                refreshTokenValue)));

        RefreshToken newRefreshToken =
                RefreshToken.builder()
                        .token(newRefreshTokenValue)
                        .userId(user.getId())
                        .expiresAt(
                                Instant.now().plusSeconds(tokenService.getRefreshTokenExpiration()))
                        .createdAt(Instant.now())
                        .build();

        refreshTokenRepository.save(newRefreshToken);

        return new LoginResponse(newAccessToken, newRefreshTokenValue);
    }
}
