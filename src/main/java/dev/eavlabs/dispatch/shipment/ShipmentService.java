package dev.eavlabs.dispatch.shipment;

import dev.eavlabs.dispatch.shared.error.ResourceConflictException;
import dev.eavlabs.dispatch.shared.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Coordinates shipment CRUD rules and transactions.
 */
@Service
@Transactional(readOnly = true)
public class ShipmentService {

    private final ShipmentRepository repository;

    /**
     * Creates the service.
     *
     * @param repository shipment persistence boundary
     */
    public ShipmentService(ShipmentRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a uniquely referenced shipment.
     */
    @Transactional
    public ShipmentResponse create(CreateShipmentRequest request) {
        var reference = normalizeReference(request.reference());
        if (repository.existsByReference(reference)) {
            throw new ResourceConflictException("Shipment reference already exists");
        }

        var shipment = Shipment.create(
                reference,
                request.description().trim(),
                request.origin().trim(),
                request.destination().trim(),
                request.scheduledPickupAt()
        );
        return ShipmentResponse.from(repository.save(shipment));
    }

    public ShipmentResponse get(UUID id) {
        return ShipmentResponse.from(find(id));
    }

    public List<ShipmentResponse> list() {
        return repository.findAll().stream().map(ShipmentResponse::from).toList();
    }

    /**
     * Updates details while preserving identity, reference, and status.
     */
    @Transactional
    public ShipmentResponse update(UUID id, UpdateShipmentRequest request) {
        var shipment = find(id);
        shipment.updateDetails(
                request.description().trim(),
                request.origin().trim(),
                request.destination().trim(),
                request.scheduledPickupAt()
        );
        return ShipmentResponse.from(shipment);
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(find(id));
    }

    private Shipment find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
    }

    private String normalizeReference(String reference) {
        return reference.trim().toUpperCase(Locale.ROOT);
    }
}
