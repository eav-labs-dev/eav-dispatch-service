package dev.eavlabs.dispatch.driver;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Persistence boundary for driver aggregates.
 */
public interface DriverRepository extends JpaRepository<Driver, UUID> {

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
