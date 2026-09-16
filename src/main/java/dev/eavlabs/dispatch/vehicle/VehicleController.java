package dev.eavlabs.dispatch.vehicle;

import dev.eavlabs.dispatch.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST API for fleet vehicle records.
 */
@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService service;

    /**
     * @param service vehicle application service
     */
    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> create(
            @Valid @RequestBody CreateVehicleRequest request
    ) {
        var body = ApiResponse.success("VEHICLE_CREATED", "Vehicle created", service.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    public ApiResponse<VehicleResponse> get(@PathVariable UUID id) {
        return ApiResponse.success("VEHICLE_RETRIEVED", "Vehicle retrieved", service.get(id));
    }

    @GetMapping
    public ApiResponse<List<VehicleResponse>> list() {
        return ApiResponse.success("VEHICLES_RETRIEVED", "Vehicles retrieved", service.list());
    }

    @PutMapping("/{id}")
    public ApiResponse<VehicleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleRequest request
    ) {
        return ApiResponse.success("VEHICLE_UPDATED", "Vehicle updated", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ApiResponse.success("VEHICLE_DELETED", "Vehicle deleted", null);
    }
}
