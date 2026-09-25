package dev.eavlabs.dispatch.shipment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/**
 * Validated request for a new shipment.
 */
public record CreateShipmentRequest(
        @NotBlank @Size(max = 64) String reference,
        @NotBlank @Size(max = 500) String description,
        @NotBlank @Size(max = 160) String origin,
        @NotBlank @Size(max = 160) String destination,
        @FutureOrPresent OffsetDateTime scheduledPickupAt
) {
}
