package dev.eavlabs.dispatch.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Persistence boundary for vehicles.
 */
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    boolean existsByRegistrationNumber(String registrationNumber);
}
