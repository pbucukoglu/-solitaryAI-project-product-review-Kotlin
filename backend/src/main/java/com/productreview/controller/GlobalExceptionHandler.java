package com.productreview.controller;

import com.productreview.exception.NotFoundException;
import com.productreview.exception.ValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Authentication failed: " + ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Invalid email or password",
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                "You don't have permission to access this resource",
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(
            ExpiredJwtException ex, WebRequest request) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Token has expired",
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handleSignatureException(
            SignatureException ex, WebRequest request) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Invalid token signature",
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            ValidationException ex, WebRequest request) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(
            NotFoundException ex, WebRequest request) {
        Map<String, Object> error = createErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (message != null && message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message != null && message.contains("FORBIDDEN")) {
            status = HttpStatus.FORBIDDEN;
        } else if (message != null && message.contains("already") || 
                   message != null && message.contains("Invalid")) {
            status = HttpStatus.BAD_REQUEST;
        }
        
        Map<String, Object> error = createErrorResponse(
                status,
                status.getReasonPhrase(),
                message,
                request.getDescription(false)
        );
        return ResponseEntity.status(status).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> error = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "Validation failed",
                request.getDescription(false)
        );
        error.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        return errorResponse;
    }
}


