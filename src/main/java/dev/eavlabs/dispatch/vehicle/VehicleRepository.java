package dev.eavlabs.dispatch.vehicle;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence boundary for vehicles.
 */
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    /**
     * Locks a vehicle while assignment eligibility is evaluated.
     *
     * @param id vehicle identifier
     * @return locked vehicle when present
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select vehicle from Vehicle vehicle where vehicle.id = :id")
    Optional<Vehicle> findByIdForUpdate(@Param("id") UUID id);

    boolean existsByRegistrationNumber(String registrationNumber);
}
