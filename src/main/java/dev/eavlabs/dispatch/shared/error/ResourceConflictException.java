package dev.eavlabs.dispatch.shared.error;

/**
 * Raised when a unique business identifier already exists.
 */
public class ResourceConflictException extends RuntimeException {

    /**
     * Creates a conflict exception.
     *
     * @param message safe client-facing explanation
     */
    public ResourceConflictException(String message) {
        super(message);
    }
}
