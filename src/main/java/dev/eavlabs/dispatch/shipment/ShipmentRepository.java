package dev.eavlabs.dispatch.shipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

/**
 * Persistence boundary for shipment aggregates.
 */
public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    /**
     * Checks the normalized business reference for uniqueness.
     *
     * @param reference normalized shipment reference
     * @return whether the reference exists
     */
    boolean existsByReference(String reference);

    /**
     * @param driverId driver identifier
     * @param statuses lifecycle states that reserve dispatch resources
     * @return whether the driver already has an active assignment
     */
    boolean existsByDriverIdAndStatusIn(UUID driverId, Collection<ShipmentStatus> statuses);

    /**
     * @param vehicleId vehicle identifier
     * @param statuses lifecycle states that reserve dispatch resources
     * @return whether the vehicle already has an active assignment
     */
    boolean existsByVehicleIdAndStatusIn(UUID vehicleId, Collection<ShipmentStatus> statuses);
}
