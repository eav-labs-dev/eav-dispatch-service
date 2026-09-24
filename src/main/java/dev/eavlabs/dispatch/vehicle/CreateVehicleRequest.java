package dev.eavlabs.dispatch.vehicle;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Validated request for a new vehicle.
 *
 * @param registrationNumber government-issued registration number
 * @param make vehicle manufacturer
 * @param model vehicle model
 * @param type vehicle category
 * @param maxPayloadKg maximum payload in kilograms
 */
public record CreateVehicleRequest(
        @NotBlank @Size(max = 32) String registrationNumber,
        @NotBlank @Size(max = 80) String make,
        @NotBlank @Size(max = 80) String model,
        @NotNull VehicleType type,
        @NotNull @Min(1) @Max(100_000) Integer maxPayloadKg
) {
}
