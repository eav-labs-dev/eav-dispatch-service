package dev.eavlabs.dispatch.driver;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence boundary for driver aggregates.
 */
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    /**
     * Locks a driver while assignment eligibility is evaluated.
     *
     * @param id driver identifier
     * @return locked driver when present
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select driver from Driver driver where driver.id = :id")
    Optional<Driver> findByIdForUpdate(@Param("id") UUID id);

    /**
     * @param employeeNumber normalized employee number
     * @return whether the employee number exists
     */
    boolean existsByEmployeeNumber(String employeeNumber);

    /**
     * @param licenseNumber normalized licence number
     * @return whether the licence number exists
     */
    boolean existsByLicenseNumber(String licenseNumber);
}
