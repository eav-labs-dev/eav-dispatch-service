package dev.eavlabs.dispatch.shipment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

/**
 * Validated editable details for an existing shipment.
 */
public record UpdateShipmentRequest(
        @NotBlank @Size(max = 500) String description,
        @NotBlank @Size(max = 160) String origin,
        @NotBlank @Size(max = 160) String destination,
        @FutureOrPresent OffsetDateTime scheduledPickupAt
) {
}
