package dev.eavlabs.dispatch.shipment;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Public shipment representation.
 */
public record ShipmentResponse(
        UUID id,
        String reference,
        String description,
        String origin,
        String destination,
        ShipmentStatus status,
        OffsetDateTime scheduledPickupAt,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Maps the persistence model to the public contract.
     *
     * @param shipment source aggregate
     * @return public representation
     */
    public static ShipmentResponse from(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getId(),
                shipment.getReference(),
                shipment.getDescription(),
                shipment.getOrigin(),
                shipment.getDestination(),
                shipment.getStatus(),
                shipment.getScheduledPickupAt(),
                shipment.getCreatedAt(),
                shipment.getUpdatedAt()
        );
    }
}
