package dev.eavlabs.dispatch.vehicle;

import dev.eavlabs.dispatch.shared.error.ResourceConflictException;
import dev.eavlabs.dispatch.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers vehicle business rules through JPA and Flyway.
 */
@SpringBootTest
@Transactional
class VehicleServiceTest {

    @Autowired
    private VehicleRepository repository;

    @Autowired
    private VehicleService service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void createsActiveVehicleWithNormalizedRegistration() {
        var response = service.create(createRequest(" gt 1234-26 "));

        assertThat(response.id()).isNotNull();
        assertThat(response.registrationNumber()).isEqualTo("GT-1234-26");
        assertThat(response.make()).isEqualTo("Mercedes-Benz");
        assertThat(response.type()).isEqualTo(VehicleType.TRUCK);
        assertThat(response.maxPayloadKg()).isEqualTo(18_000);
        assertThat(response.active()).isTrue();
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void rejectsDuplicateRegistrationNumber() {
        service.create(createRequest("GT 1234-26"));

        assertThatThrownBy(() -> service.create(createRequest(" gt-1234-26 ")))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Vehicle registration number already exists");
    }

    @Test
    void updatesMutableDetailsAndCanDeactivateVehicle() {
        var created = service.create(createRequest("GT 1234-26"));

        var updated = service.update(created.id(), new UpdateVehicleRequest(
                " Volvo ", "FH16", VehicleType.TRAILER, 30_000, false
        ));

        assertThat(updated.registrationNumber()).isEqualTo(created.registrationNumber());
        assertThat(updated.make()).isEqualTo("Volvo");
        assertThat(updated.model()).isEqualTo("FH16");
        assertThat(updated.type()).isEqualTo(VehicleType.TRAILER);
        assertThat(updated.maxPayloadKg()).isEqualTo(30_000);
        assertThat(updated.active()).isFalse();
        assertThat(updated.updatedAt()).isEqualTo(repository.findById(created.id()).orElseThrow().getUpdatedAt());
    }

    @Test
    void reportsMissingVehicleAndDeletesPersistedVehicle() {
        assertThatThrownBy(() -> service.get(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Vehicle not found");

        var created = service.create(createRequest("GT 1234-26"));
        service.delete(created.id());
        assertThat(repository.existsById(created.id())).isFalse();
    }

    private CreateVehicleRequest createRequest(String registrationNumber) {
        return new CreateVehicleRequest(
                registrationNumber,
                " Mercedes-Benz ",
                "Actros",
                VehicleType.TRUCK,
                18_000
        );
    }
}
