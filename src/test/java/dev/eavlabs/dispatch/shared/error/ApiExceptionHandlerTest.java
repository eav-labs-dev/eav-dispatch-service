package dev.eavlabs.dispatch.shared.error;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that common request and persistence failures retain the public API envelope.
 */
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void normalizesMalformedRequestBodies() {
        var exception = new HttpMessageNotReadableException(
                "Invalid JSON",
                new MockHttpInputMessage(new byte[0])
        );

        var response = handler.handleMalformedRequest(exception);

        assertFailure(response.getStatusCode().value(), response.getBody(), "MALFORMED_REQUEST");
    }

    @Test
    void normalizesDatabaseConstraintConflicts() {
        var exception = new DataIntegrityViolationException("sensitive database details");

        var response = handler.handleDataIntegrityConflict(exception);

        assertFailure(response.getStatusCode().value(), response.getBody(), "DATA_CONFLICT");
        assertThat(response.getBody().message()).doesNotContain("database");
    }

    @Test
    void normalizesInvalidPathParameters() {
        var exception = new MethodArgumentTypeMismatchException(
                "not-a-uuid",
                UUID.class,
                "id",
                null,
                new IllegalArgumentException("Invalid UUID")
        );

        var response = handler.handleTypeMismatch(exception);

        assertFailure(response.getStatusCode().value(), response.getBody(), "INVALID_PARAMETER");
        assertThat(response.getBody().error().fields()).containsEntry("id", "Invalid value");
        assertThat(response.getBody().message()).doesNotContain("not-a-uuid");
    }

    private void assertFailure(
            int status,
            dev.eavlabs.dispatch.shared.api.ApiResponse<Void> body,
            String expectedCode
    ) {
        assertThat(status).isIn(HttpStatus.BAD_REQUEST.value(), HttpStatus.CONFLICT.value());
        assertThat(body).isNotNull();
        assertThat(body.success()).isFalse();
        assertThat(body.code()).isEqualTo(expectedCode);
        assertThat(body.data()).isNull();
        assertThat(body.error().type()).isEqualTo(expectedCode);
        assertThat(body.error().fields()).isEmpty();
    }
}
