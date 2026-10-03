package dev.eavlabs.dispatch.shared.http;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configurable HTTP safeguards for the single-instance MVP deployment.
 *
 * @param requestsPerMinute requests accepted from one client address per minute
 * @param maxRequestBodyBytes largest declared request body accepted by the API
 */
@Validated
@ConfigurationProperties(prefix = "dispatch.http")
public record HttpProtectionProperties(
        @Min(1) int requestsPerMinute,
        @Min(1) long maxRequestBodyBytes
) {
}
