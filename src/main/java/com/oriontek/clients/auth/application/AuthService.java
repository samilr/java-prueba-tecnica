package com.oriontek.clients.auth.application;

import com.oriontek.clients.auth.api.dto.AuthResponse;
import com.oriontek.clients.auth.api.dto.LoginRequest;
import com.oriontek.clients.auth.api.dto.RefreshRequest;
import com.oriontek.clients.auth.api.dto.RegisterRequest;
import com.oriontek.clients.auth.domain.Role;
import com.oriontek.clients.auth.domain.User;
import com.oriontek.clients.auth.domain.UserRepository;
import com.oriontek.clients.shared.exception.ConflictException;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import com.oriontek.clients.shared.security.JwtService;
import com.oriontek.clients.shared.security.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user =
                userRepository
                        .findByUsername(request.username())
                        .or(() -> userRepository.findByEmailIgnoreCase(request.username()))
                        .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new ConflictException(
                    "El nombre de usuario ya está en uso: " + request.username());
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("El email ya está registrado: " + request.email());
        }
        User user =
                User.create(
                        request.username(),
                        request.email(),
                        passwordEncoder.encode(request.password()),
                        Role.USER);
        return issueTokens(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isValid(token) || jwtService.extractTokenType(token) != TokenType.REFRESH) {
            throw new BadCredentialsException("Refresh token inválido o expirado");
        }
        String username = jwtService.extractUsername(token);
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> ResourceNotFoundException.of("Usuario", username));
        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        String role = user.getRole().name();
        String accessToken = jwtService.generateAccessToken(user.getUsername(), role);
        String refreshToken = jwtService.generateRefreshToken(user.getUsername(), role);
        return AuthResponse.of(
                accessToken, refreshToken, jwtService.accessTokenExpirationSeconds());
    }
}
