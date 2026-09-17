package dev.eavlabs.dispatch.shipment;

import dev.eavlabs.dispatch.driver.CreateDriverRequest;
import dev.eavlabs.dispatch.driver.DriverService;
import dev.eavlabs.dispatch.vehicle.CreateVehicleRequest;
import dev.eavlabs.dispatch.vehicle.VehicleService;
import dev.eavlabs.dispatch.vehicle.VehicleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the complete dispatch workflow against the PostgreSQL version used by Compose.
 */
@Testcontainers
@SpringBootTest
class DispatchPostgreSqlIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void persistsAssignmentLifecycleAndAuditHistory() {
        var driver = driverService.create(new CreateDriverRequest(
                "DRV-PG-001",
                "Ama Mensah",
                "+233 24 000 0001",
                "LIC-PG-001",
                "C",
                LocalDate.now().plusYears(2)
        ));
        var vehicle = vehicleService.create(new CreateVehicleRequest(
                "GT PG 1001",
                "Volvo",
                "FH16",
                VehicleType.TRUCK,
                25_000
        ));
        var shipment = shipmentService.create(new CreateShipmentRequest(
                "DSP-PG-001",
                "Medical equipment",
                "Tema",
                "Kumasi",
                OffsetDateTime.now().plusDays(1)
        ));

        var assigned = shipmentService.assign(
                shipment.id(),
                new AssignShipmentRequest(driver.id(), vehicle.id())
        );
        shipmentService.transition(
                shipment.id(),
                new TransitionShipmentRequest(ShipmentStatus.IN_TRANSIT, "Departed Tema")
        );
        var delivered = shipmentService.transition(
                shipment.id(),
                new TransitionShipmentRequest(ShipmentStatus.DELIVERED, "Received in Kumasi")
        );

        var appliedMigrations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history "
                        + "WHERE version IN ('1', '2', '3', '4') AND success = true",
                Integer.class
        );

        assertThat(POSTGRES.isRunning()).isTrue();
        assertThat(appliedMigrations).isEqualTo(4);
        assertThat(assigned.driverId()).isEqualTo(driver.id());
        assertThat(assigned.vehicleId()).isEqualTo(vehicle.id());
        assertThat(delivered.status()).isEqualTo(ShipmentStatus.DELIVERED);
        assertThat(shipmentService.history(shipment.id()))
                .extracting(ShipmentStatusHistoryResponse::newStatus)
                .containsExactly(
                        ShipmentStatus.CREATED,
                        ShipmentStatus.ASSIGNED,
                        ShipmentStatus.IN_TRANSIT,
                        ShipmentStatus.DELIVERED
                );
    }
}
