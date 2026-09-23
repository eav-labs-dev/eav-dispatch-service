package dev.eavlabs.dispatch.driver;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Validated request for a new driver.
 *
 * @param employeeNumber organization-issued driver identifier
 * @param fullName driver's full name
 * @param phoneNumber driver's contact number
 * @param licenseNumber government-issued driving licence identifier
 * @param licenseClass class of vehicles the driver may operate
 * @param licenseExpiresOn licence expiry date
 */
public record CreateDriverRequest(
        @NotBlank @Size(max = 32) String employeeNumber,
        @NotBlank @Size(max = 160) String fullName,
        @NotBlank @Pattern(regexp = "^[+]?[0-9][0-9 ()-]{6,24}$") String phoneNumber,
        @NotBlank @Size(max = 64) String licenseNumber,
        @NotBlank @Size(max = 16) String licenseClass,
        @NotNull @FutureOrPresent LocalDate licenseExpiresOn
) {
}
