package dev.eavlabs.dispatch.shared.error;

import dev.eavlabs.dispatch.shared.api.ApiError;
import dev.eavlabs.dispatch.shared.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
