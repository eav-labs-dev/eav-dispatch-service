package dev.eavlabs.dispatch.driver;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Validated editable details for a driver.
 *
 * @param fullName driver's full name
 * @param phoneNumber driver's contact number
 * @param licenseClass class of vehicles the driver may operate
 * @param licenseExpiresOn licence expiry date
 * @param active whether the driver may receive new assignments
 */
public record UpdateDriverRequest(
        @NotBlank @Size(max = 160) String fullName,
        @NotBlank @Pattern(regexp = "^[+]?[0-9][0-9 ()-]{6,24}$") String phoneNumber,
        @NotBlank @Size(max = 16) String licenseClass,
        @NotNull @FutureOrPresent LocalDate licenseExpiresOn,
        @NotNull Boolean active
) {
}
