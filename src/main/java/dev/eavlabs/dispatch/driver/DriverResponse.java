package dev.eavlabs.dispatch.driver;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Public driver representation.
 *
 * @param id driver identifier
 * @param employeeNumber organization-issued driver identifier
 * @param fullName driver's full name
 * @param phoneNumber driver's contact number
 * @param licenseNumber government-issued driving licence identifier
 * @param licenseClass class of vehicles the driver may operate
 * @param licenseExpiresOn licence expiry date
 * @param active whether the driver may receive new assignments
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
public record DriverResponse(
        UUID id,
        String employeeNumber,
        String fullName,
        String phoneNumber,
        String licenseNumber,
        String licenseClass,
        LocalDate licenseExpiresOn,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * @param driver source aggregate
     * @return public representation
     */
    public static DriverResponse from(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getEmployeeNumber(),
                driver.getFullName(),
                driver.getPhoneNumber(),
                driver.getLicenseNumber(),
                driver.getLicenseClass(),
                driver.getLicenseExpiresOn(),
                driver.isActive(),
                driver.getCreatedAt(),
                driver.getUpdatedAt()
        );
    }
}
