package dev.eavlabs.dispatch.driver;

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
 * REST API for driver records.
 */
@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService service;

    /**
     * @param service driver application service
     */
    public DriverController(DriverService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DriverResponse>> create(
            @Valid @RequestBody CreateDriverRequest request
    ) {
        var body = ApiResponse.success("DRIVER_CREATED", "Driver created", service.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    public ApiResponse<DriverResponse> get(@PathVariable UUID id) {
        return ApiResponse.success("DRIVER_RETRIEVED", "Driver retrieved", service.get(id));
    }

    @GetMapping
    public ApiResponse<List<DriverResponse>> list() {
        return ApiResponse.success("DRIVERS_RETRIEVED", "Drivers retrieved", service.list());
    }

    @PutMapping("/{id}")
    public ApiResponse<DriverResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDriverRequest request
    ) {
        return ApiResponse.success("DRIVER_UPDATED", "Driver updated", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ApiResponse.success("DRIVER_DELETED", "Driver deleted", null);
    }
}
