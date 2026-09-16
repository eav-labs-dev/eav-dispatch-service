package dev.eavlabs.dispatch.shipment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Validated request for a controlled lifecycle transition.
 *
 * @param targetStatus requested next status
 * @param note optional operational context for the audit trail
 */
public record TransitionShipmentRequest(
        @NotNull ShipmentStatus targetStatus,
        @Size(max = 500) String note
) {
}
