package dev.eavlabs.dispatch.shipment;

/**
 * Shipment states reserved for the controlled lifecycle milestone.
 */
public enum ShipmentStatus {
    CREATED,
    ASSIGNED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED
}
