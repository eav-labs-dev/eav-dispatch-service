package dev.eavlabs.dispatch.shipment;

import dev.eavlabs.dispatch.driver.DriverRepository;
import dev.eavlabs.dispatch.shared.error.ResourceConflictException;
import dev.eavlabs.dispatch.shared.error.ResourceNotFoundException;
import dev.eavlabs.dispatch.vehicle.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final ShipmentStatusHistoryRepository historyRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * Creates the service.
     *
     * @param repository shipment persistence boundary
     * @param historyRepository lifecycle audit persistence boundary
     * @param driverRepository driver persistence boundary
     * @param vehicleRepository vehicle persistence boundary
     */
    public ShipmentService(
            ShipmentRepository repository,
            ShipmentStatusHistoryRepository historyRepository,
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository
    ) {
        this.repository = repository;
        this.historyRepository = historyRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
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
        var saved = repository.save(shipment);
        historyRepository.save(ShipmentStatusHistory.record(
                saved, null, ShipmentStatus.CREATED, "Shipment created"
        ));
        return ShipmentResponse.from(saved);
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
        if (shipment.getStatus() == ShipmentStatus.DELIVERED
                || shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new ResourceConflictException("Terminal shipments cannot be edited");
        }
        shipment.updateDetails(
                request.description().trim(),
                request.origin().trim(),
                request.destination().trim(),
                request.scheduledPickupAt()
        );
        return ShipmentResponse.from(shipment);
    }

    /**
     * Assigns an eligible driver and vehicle to a created shipment.
     *
     * @param id shipment identifier
     * @param request assignment resource identifiers
     * @return assigned shipment
     */
    @Transactional
    public ShipmentResponse assign(UUID id, AssignShipmentRequest request) {
        var shipment = find(id);
        var driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        var vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!driver.isActive()) {
            throw new ResourceConflictException("Inactive driver cannot be assigned");
        }
        if (driver.getLicenseExpiresOn().isBefore(LocalDate.now())) {
            throw new ResourceConflictException("Driver licence is expired");
        }
        if (!vehicle.isActive()) {
            throw new ResourceConflictException("Inactive vehicle cannot be assigned");
        }

        var previousStatus = shipment.getStatus();
        shipment.assign(driver, vehicle);
        historyRepository.save(ShipmentStatusHistory.record(
                shipment, previousStatus, shipment.getStatus(), "Driver and vehicle assigned"
        ));
        repository.flush();
        return ShipmentResponse.from(shipment);
    }

    /**
     * Applies a permitted lifecycle transition and records it.
     *
     * @param id shipment identifier
     * @param request requested transition
     * @return transitioned shipment
     */
    @Transactional
    public ShipmentResponse transition(UUID id, TransitionShipmentRequest request) {
        var shipment = find(id);
        var previousStatus = shipment.getStatus();
        shipment.transitionTo(request.targetStatus());
        historyRepository.save(ShipmentStatusHistory.record(
                shipment, previousStatus, shipment.getStatus(), request.note()
        ));
        repository.flush();
        return ShipmentResponse.from(shipment);
    }

    /**
     * @param id shipment identifier
     * @return chronological lifecycle audit history
     */
    public List<ShipmentStatusHistoryResponse> history(UUID id) {
        find(id);
        return historyRepository.findByShipmentIdOrderByChangedAtAsc(id).stream()
                .map(ShipmentStatusHistoryResponse::from)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        var shipment = find(id);
        historyRepository.deleteAllByShipmentId(id);
        historyRepository.flush();
        repository.delete(shipment);
    }

    private Shipment find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found"));
    }

    private String normalizeReference(String reference) {
        return reference.trim().toUpperCase(Locale.ROOT);
    }
}
