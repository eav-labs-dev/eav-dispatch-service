package dev.eavlabs.dispatch.shared.error;

/**
 * Raised when a requested domain resource does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a not-found exception.
     *
     * @param message safe client-facing explanation
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
