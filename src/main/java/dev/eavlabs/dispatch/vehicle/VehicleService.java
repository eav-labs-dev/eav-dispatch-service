package dev.eavlabs.dispatch.vehicle;

import dev.eavlabs.dispatch.shared.error.ResourceConflictException;
import dev.eavlabs.dispatch.shared.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Coordinates vehicle CRUD rules and transactions.
 */
@Service
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository repository;

    /**
     * @param repository vehicle persistence boundary
     */
    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    /**
     * @param request validated vehicle details
     * @return persisted vehicle
     */
    @Transactional
    public VehicleResponse create(CreateVehicleRequest request) {
        var registrationNumber = normalizeRegistration(request.registrationNumber());
        if (repository.existsByRegistrationNumber(registrationNumber)) {
            throw new ResourceConflictException("Vehicle registration number already exists");
        }

        var vehicle = Vehicle.create(
                registrationNumber,
                request.make().trim(),
                request.model().trim(),
                request.type(),
                request.maxPayloadKg()
        );
        return VehicleResponse.from(repository.save(vehicle));
    }

    /**
     * @param id vehicle identifier
     * @return requested vehicle
     */
    public VehicleResponse get(UUID id) {
        return VehicleResponse.from(find(id));
    }

    /**
     * @return all vehicles
     */
    public List<VehicleResponse> list() {
        return repository.findAll().stream().map(VehicleResponse::from).toList();
    }

    /**
     * @param id vehicle identifier
     * @param request validated editable details
     * @return updated vehicle
     */
    @Transactional
    public VehicleResponse update(UUID id, UpdateVehicleRequest request) {
        var vehicle = find(id);
        vehicle.updateDetails(
                request.make().trim(),
                request.model().trim(),
                request.type(),
                request.maxPayloadKg(),
                request.active()
        );
        repository.flush();
        return VehicleResponse.from(vehicle);
    }

    /**
     * @param id vehicle identifier
     */
    @Transactional
    public void delete(UUID id) {
        repository.delete(find(id));
    }

    private Vehicle find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
    }

    private String normalizeRegistration(String value) {
        return value.trim().replaceAll("\\s+", "-").toUpperCase(Locale.ROOT);
    }
}
