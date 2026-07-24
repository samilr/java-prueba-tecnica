package com.oriontek.clients.auth.api;

import com.oriontek.clients.auth.api.dto.AuthResponse;
import com.oriontek.clients.auth.api.dto.LoginRequest;
import com.oriontek.clients.auth.api.dto.RefreshRequest;
import com.oriontek.clients.auth.api.dto.RegisterRequest;
import com.oriontek.clients.auth.application.AuthService;
import com.oriontek.clients.shared.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Registro, login y renovación de tokens")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthApiMapper authApiMapper;

    @PostMapping("/login")
    @Operation(summary = "Autenticar y obtener tokens de acceso y refresco")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(
                authApiMapper.toResponse(
                        authService.login(request.username(), request.password())));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario con rol USER")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse tokens =
                authApiMapper.toResponse(
                        authService.register(
                                request.username(), request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(tokens));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar el access token usando un refresh token")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok(
                authApiMapper.toResponse(authService.refresh(request.refreshToken())));
    }
}
