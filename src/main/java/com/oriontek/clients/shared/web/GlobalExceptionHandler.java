package com.oriontek.clients.shared.web;

import com.oriontek.clients.shared.exception.BusinessRuleException;
import com.oriontek.clients.shared.exception.ConflictException;
import com.oriontek.clients.shared.exception.DomainException;
import com.oriontek.clients.shared.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiError.FieldError> errors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                error ->
                                        new ApiError.FieldError(
                                                error.getField(),
                                                error.getDefaultMessage() == null
                                                        ? "inválido"
                                                        : error.getDefaultMessage()))
                        .toList();
        return build(
                ApiError.of(
                        HttpStatus.BAD_REQUEST,
                        "Error de validación",
                        "Uno o más campos son inválidos",
                        errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex) {
        return build(ApiError.of(HttpStatus.BAD_REQUEST, "Error de validación", ex.getMessage()));
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        return build(
                ApiError.of(
                        HttpStatus.BAD_REQUEST,
                        "Solicitud inválida",
                        "El cuerpo o los parámetros de la solicitud no son válidos"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return build(ApiError.of(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage()));
    }

    @ExceptionHandler({ConflictException.class, BusinessRuleException.class})
    public ResponseEntity<ApiResponse<Void>> handleConflict(DomainException ex) {
        return build(
                ApiError.of(
                        HttpStatus.CONFLICT, "Conflicto con el estado actual", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
        return build(
                ApiError.of(
                        HttpStatus.CONFLICT,
                        "Conflicto de datos",
                        "La operación viola una restricción de unicidad o integridad"));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(
            OptimisticLockingFailureException ex) {
        return build(
                ApiError.of(
                        HttpStatus.CONFLICT,
                        "Conflicto de concurrencia",
                        "El recurso fue modificado por otra operación, intente nuevamente"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return build(
                ApiError.of(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas",
                        "Usuario o contraseña incorrectos"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        return build(
                ApiError.of(HttpStatus.UNAUTHORIZED, "No autenticado", "Autenticación requerida"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return build(
                ApiError.of(
                        HttpStatus.FORBIDDEN,
                        "Acceso denegado",
                        "No tiene permisos para esta operación"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(
            Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(
                ApiError.of(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error interno",
                        "Ocurrió un error inesperado, contacte al administrador"));
    }

    private ResponseEntity<ApiResponse<Void>> build(ApiError error) {
        return ResponseEntity.status(error.status()).body(ApiResponse.failure(error));
    }
}
