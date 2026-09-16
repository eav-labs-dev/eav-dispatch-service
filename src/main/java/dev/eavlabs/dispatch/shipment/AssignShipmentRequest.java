package dev.eavlabs.dispatch.shipment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Validated request to assign dispatch resources to a shipment.
 *
 * @param driverId active driver identifier
 * @param vehicleId active vehicle identifier
 */
public record AssignShipmentRequest(
        @NotNull UUID driverId,
        @NotNull UUID vehicleId
) {
}
