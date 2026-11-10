package com.wallet.ledger.exception;

import com.wallet.ledger.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleWalletNotFound(WalletNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(DuplicateIdempotencyKeyException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateIdempotencyKey(DuplicateIdempotencyKeyException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTransaction(InvalidTransactionException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLockingFailure(OptimisticLockingFailureException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Concurrent transaction conflict detected. Please retry your request.", request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI(), errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred: " + ex.getMessage(), request.getRequestURI(), null);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, String path, List<String> details) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .details(details)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
