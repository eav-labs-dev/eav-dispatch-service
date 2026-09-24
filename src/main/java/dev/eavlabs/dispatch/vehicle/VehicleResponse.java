package dev.eavlabs.dispatch.vehicle;

import java.time.Instant;
import java.util.UUID;

/**
 * API projection of a vehicle.
 */
public record VehicleResponse(
        UUID id,
        String registrationNumber,
        String make,
        String model,
        VehicleType type,
        int maxPayloadKg,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * @param vehicle persisted vehicle
     * @return API projection
     */
    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getRegistrationNumber(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getType(),
                vehicle.getMaxPayloadKg(),
                vehicle.isActive(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}
