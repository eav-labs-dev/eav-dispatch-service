package dev.eavlabs.dispatch.shipment;

import java.time.Instant;
import java.util.UUID;

/**
 * API projection of a shipment lifecycle audit entry.
 */
public record ShipmentStatusHistoryResponse(
        UUID id,
        ShipmentStatus previousStatus,
        ShipmentStatus newStatus,
        String note,
        Instant changedAt
) {

    /**
     * @param history persisted lifecycle entry
     * @return API projection
     */
    public static ShipmentStatusHistoryResponse from(ShipmentStatusHistory history) {
        return new ShipmentStatusHistoryResponse(
                history.getId(),
                history.getPreviousStatus(),
                history.getNewStatus(),
                history.getNote(),
                history.getChangedAt()
        );
    }
}
