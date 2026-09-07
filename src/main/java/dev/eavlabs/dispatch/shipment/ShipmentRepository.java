package dev.eavlabs.dispatch.shipment;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
