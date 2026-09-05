package dev.eavlabs.dispatch.health;

import dev.eavlabs.dispatch.shared.api.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes a lightweight application health contract.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final String version;

    /**
     * Creates the health controller.
     *
     * @param version configured application version
     */
    public HealthController(@Value("${info.app.version:development}") String version) {
        this.version = version;
    }

    /**
     * Returns the public health state.
     *
     * @return successful health response
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ApplicationHealth>> getHealth() {
        var health = new ApplicationHealth("eav-dispatch-service", "UP", version);
        return ResponseEntity.ok(ApiResponse.success("HEALTHY", "Service is available", health));
    }
}
