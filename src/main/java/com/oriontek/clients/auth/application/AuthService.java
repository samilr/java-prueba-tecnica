package com.oriontek.clients.auth.application;

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
    public AuthTokens login(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        User user =
                userRepository
                        .findByUsername(username)
                        .or(() -> userRepository.findByEmail(username))
                        .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        return issueTokens(user);
    }

    @Transactional
    public AuthTokens register(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("El nombre de usuario ya está en uso: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("El email ya está registrado: " + email);
        }
        User user = User.create(username, email, passwordEncoder.encode(password), Role.USER);
        return issueTokens(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthTokens refresh(String refreshToken) {
        if (!jwtService.isValid(refreshToken)
                || jwtService.extractTokenType(refreshToken) != TokenType.REFRESH) {
            throw new BadCredentialsException("Refresh token inválido o expirado");
        }
        String username = jwtService.extractUsername(refreshToken);
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> ResourceNotFoundException.of("Usuario", username));
        return issueTokens(user);
    }

    private AuthTokens issueTokens(User user) {
        String role = user.getRole().name();
        return new AuthTokens(
                jwtService.generateAccessToken(user.getUsername(), role),
                jwtService.generateRefreshToken(user.getUsername(), role),
                jwtService.accessTokenExpirationSeconds());
    }
}
