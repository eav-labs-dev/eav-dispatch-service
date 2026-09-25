package dev.eavlabs.dispatch.shipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Persistence boundary for shipment lifecycle audit entries.
 */
public interface ShipmentStatusHistoryRepository extends JpaRepository<ShipmentStatusHistory, UUID> {

    List<ShipmentStatusHistory> findByShipmentIdOrderByChangedAtAsc(UUID shipmentId);

    void deleteAllByShipmentId(UUID shipmentId);
}
