package dev.eavlabs.dispatch.shared.error;

import dev.eavlabs.dispatch.shared.api.ApiError;
import dev.eavlabs.dispatch.shared.api.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Normalizes expected API failures without leaking implementation details.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Maps missing resources to a stable 404 response.
     *
     * @param exception domain exception
     * @return normalized response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException exception) {
        return failure(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", exception.getMessage(), Map.of());
    }

    /**
     * Maps uniqueness conflicts to a stable 409 response.
     *
     * @param exception domain exception
     * @return normalized response
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(ResourceConflictException exception) {
        return failure(HttpStatus.CONFLICT, "RESOURCE_CONFLICT", exception.getMessage(), Map.of());
    }

    /**
     * Maps bean-validation failures to field-level details.
     *
     * @param exception validation exception
     * @return normalized response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return failure(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request validation failed", fields);
    }

    /**
     * Maps unreadable JSON and invalid enum values to the public error envelope.
     *
     * @param exception request deserialization exception
     * @return normalized response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedRequest(HttpMessageNotReadableException exception) {
        return failure(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST",
                "Request body is malformed or contains an invalid value",
                Map.of()
        );
    }

    /**
     * Maps invalid path and query parameter types to the public error envelope.
     *
     * @param exception parameter conversion exception
     * @return normalized response
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return failure(
                HttpStatus.BAD_REQUEST,
                "INVALID_PARAMETER",
                "Request parameter has an invalid value",
                Map.of(exception.getName(), "Invalid value")
        );
    }

    /**
     * Maps database constraint races and protected relationships without leaking SQL details.
     *
     * @param exception persistence constraint exception
     * @return normalized response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityConflict(
            DataIntegrityViolationException exception
    ) {
        return failure(
                HttpStatus.CONFLICT,
                "DATA_CONFLICT",
                "The request conflicts with persisted data",
                Map.of()
        );
    }

    private ResponseEntity<ApiResponse<Void>> failure(
            HttpStatus status,
            String code,
            String message,
            Map<String, String> fields
    ) {
        var error = new ApiError(code, fields);
        return ResponseEntity.status(status).body(ApiResponse.failure(code, message, error));
    }
}
