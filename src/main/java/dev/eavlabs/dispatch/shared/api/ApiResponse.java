package dev.eavlabs.dispatch.shared.api;

/**
 * Stable response envelope used by public JSON endpoints.
 *
 * @param success whether the operation completed successfully
 * @param code machine-readable result code
 * @param message human-readable result summary
 * @param data response payload
 * @param <T> payload type
 */
public record ApiResponse<T>(boolean success, String code, String message, T data) {

    /**
     * Creates a successful response envelope.
     *
     * @param code machine-readable result code
     * @param message human-readable result summary
     * @param data response payload
     * @param <T> payload type
     * @return populated response envelope
     */
    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }
}
