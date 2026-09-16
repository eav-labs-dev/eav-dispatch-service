package dev.eavlabs.dispatch.vehicle;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Validated editable details for a vehicle.
 *
 * @param make vehicle manufacturer
 * @param model vehicle model
 * @param type vehicle category
 * @param maxPayloadKg maximum payload in kilograms
 * @param active whether the vehicle may receive new assignments
 */
public record UpdateVehicleRequest(
        @NotBlank @Size(max = 80) String make,
        @NotBlank @Size(max = 80) String model,
        @NotNull VehicleType type,
        @NotNull @Min(1) @Max(100_000) Integer maxPayloadKg,
        @NotNull Boolean active
) {
}
