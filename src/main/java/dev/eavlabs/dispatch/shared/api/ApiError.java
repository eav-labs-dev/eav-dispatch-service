package dev.eavlabs.dispatch.shared.api;

import java.util.Map;

/**
 * Structured details for a failed API request.
 *
 * @param type stable error category
 * @param fields optional field-level validation messages
 */
public record ApiError(String type, Map<String, String> fields) {
}
