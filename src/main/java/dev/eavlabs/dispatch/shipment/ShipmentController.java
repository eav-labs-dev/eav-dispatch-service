package dev.eavlabs.dispatch.shipment;

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
 * REST API for shipment records.
 */
@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService service;

    public ShipmentController(ShipmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentResponse>> create(
            @Valid @RequestBody CreateShipmentRequest request
    ) {
        var body = ApiResponse.success("SHIPMENT_CREATED", "Shipment created", service.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    public ApiResponse<ShipmentResponse> get(@PathVariable UUID id) {
        return ApiResponse.success("SHIPMENT_RETRIEVED", "Shipment retrieved", service.get(id));
    }

    @GetMapping
    public ApiResponse<List<ShipmentResponse>> list() {
        return ApiResponse.success("SHIPMENTS_RETRIEVED", "Shipments retrieved", service.list());
    }

    @PutMapping("/{id}")
    public ApiResponse<ShipmentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShipmentRequest request
    ) {
        return ApiResponse.success("SHIPMENT_UPDATED", "Shipment updated", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ApiResponse.success("SHIPMENT_DELETED", "Shipment deleted", null);
    }
}
