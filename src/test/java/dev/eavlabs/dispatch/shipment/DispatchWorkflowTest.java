package dev.eavlabs.dispatch.shipment;

import dev.eavlabs.dispatch.driver.CreateDriverRequest;
import dev.eavlabs.dispatch.driver.DriverService;
import dev.eavlabs.dispatch.shared.error.ResourceConflictException;
import dev.eavlabs.dispatch.vehicle.CreateVehicleRequest;
import dev.eavlabs.dispatch.vehicle.VehicleService;
import dev.eavlabs.dispatch.vehicle.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers resource assignment, lifecycle guards, and persisted audit history.
 */
@SpringBootTest
@Transactional
class DispatchWorkflowTest {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ShipmentStatusHistoryRepository historyRepository;

    @BeforeEach
    void setUp() {
        historyRepository.deleteAll();
    }

    @Test
    void assignsResourcesAndCompletesControlledLifecycle() {
        var shipment = createShipment("WF-001");
        var driver = driverService.create(new CreateDriverRequest(
                "DRV-WF-001", "Kwame Boateng", "+233 24 123 4567",
                "LIC-WF-001", "C", LocalDate.now().plusYears(2)
        ));
        var vehicle = vehicleService.create(new CreateVehicleRequest(
                "GT WF 001", "Volvo", "FH16", VehicleType.TRUCK, 25_000
        ));

        var assigned = shipmentService.assign(
                shipment.id(), new AssignShipmentRequest(driver.id(), vehicle.id())
        );
        var inTransit = shipmentService.transition(
                shipment.id(), new TransitionShipmentRequest(ShipmentStatus.IN_TRANSIT, "Departed Accra")
        );
        var delivered = shipmentService.transition(
                shipment.id(), new TransitionShipmentRequest(ShipmentStatus.DELIVERED, "Received in Kumasi")
        );

        assertThat(assigned.status()).isEqualTo(ShipmentStatus.ASSIGNED);
        assertThat(assigned.driverId()).isEqualTo(driver.id());
        assertThat(assigned.vehicleId()).isEqualTo(vehicle.id());
        assertThat(inTransit.status()).isEqualTo(ShipmentStatus.IN_TRANSIT);
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

    @Test
    void rejectsSkippedAndTerminalTransitions() {
        var shipment = createShipment("WF-002");

        assertThatThrownBy(() -> shipmentService.transition(
                shipment.id(), new TransitionShipmentRequest(ShipmentStatus.IN_TRANSIT, null)
        ))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Shipment cannot transition from CREATED to IN_TRANSIT");

        shipmentService.transition(
                shipment.id(), new TransitionShipmentRequest(ShipmentStatus.CANCELLED, "Customer request")
        );
        assertThatThrownBy(() -> shipmentService.transition(
                shipment.id(), new TransitionShipmentRequest(ShipmentStatus.CREATED, null)
        ))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Shipment cannot transition from CANCELLED to CREATED");
    }

    private ShipmentResponse createShipment(String reference) {
        return shipmentService.create(new CreateShipmentRequest(
                reference,
                "Equipment delivery",
                "Accra",
                "Kumasi",
                OffsetDateTime.now().plusDays(1)
        ));
    }
}
