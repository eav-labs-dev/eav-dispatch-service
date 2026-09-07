package dev.eavlabs.dispatch.shipment;

import dev.eavlabs.dispatch.shared.error.ResourceConflictException;
import dev.eavlabs.dispatch.shared.error.ResourceNotFoundException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers shipment business rules through the same JPA and migration path used by the service.
 */
@SpringBootTest
@Transactional
class ShipmentServiceTest {

    @Autowired
    private ShipmentRepository repository;

    @Autowired
    private ShipmentService service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void createsShipmentWithNormalizedReferenceAndInitialStatus() {
        var request = createRequest(" gh-001 ");

        var response = service.create(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.reference()).isEqualTo("GH-001");
        assertThat(response.status()).isEqualTo(ShipmentStatus.CREATED);
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    void rejectsDuplicateReference() {
        service.create(createRequest("GH-001"));

        assertThatThrownBy(() -> service.create(createRequest(" gh-001 ")))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Shipment reference already exists");
    }

    @Test
    void reportsMissingShipment() {
        var id = UUID.randomUUID();

        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Shipment not found");
    }

    @Test
    void deletesPersistedShipment() {
        var shipment = service.create(createRequest("GH-001"));

        service.delete(shipment.id());

        assertThat(repository.existsById(shipment.id())).isFalse();
    }

    private CreateShipmentRequest createRequest(String reference) {
        return new CreateShipmentRequest(
                reference,
                "Equipment delivery",
                "Accra",
                "Kumasi",
                OffsetDateTime.now().plusDays(1)
        );
    }
}
