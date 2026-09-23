package dev.eavlabs.dispatch.driver;

import dev.eavlabs.dispatch.shared.error.ResourceConflictException;
import dev.eavlabs.dispatch.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers driver business rules through JPA and Flyway.
 */
@SpringBootTest
@Transactional
class DriverServiceTest {

    @Autowired
    private DriverRepository repository;

    @Autowired
    private DriverService service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void createsActiveDriverWithNormalizedIdentifiers() {
        var response = service.create(createRequest(" drv-001 ", " gh-dl-001 "));

        assertThat(response.id()).isNotNull();
        assertThat(response.employeeNumber()).isEqualTo("DRV-001");
        assertThat(response.licenseNumber()).isEqualTo("GH-DL-001");
        assertThat(response.licenseClass()).isEqualTo("B");
        assertThat(response.active()).isTrue();
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void rejectsDuplicateEmployeeNumberAndLicenceNumber() {
        service.create(createRequest("DRV-001", "GH-DL-001"));

        assertThatThrownBy(() -> service.create(createRequest(" drv-001 ", "GH-DL-002")))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Driver employee number already exists");
        assertThatThrownBy(() -> service.create(createRequest("DRV-002", " gh-dl-001 ")))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Driver licence number already exists");
    }

    @Test
    void updatesMutableDetailsAndCanDeactivateDriver() {
        var created = service.create(createRequest("DRV-001", "GH-DL-001"));

        var updated = service.update(created.id(), new UpdateDriverRequest(
                " Ama Mensah ", "+233 24 999 0000", " c ", LocalDate.now().plusYears(3), false
        ));

        assertThat(updated.employeeNumber()).isEqualTo(created.employeeNumber());
        assertThat(updated.licenseNumber()).isEqualTo(created.licenseNumber());
        assertThat(updated.fullName()).isEqualTo("Ama Mensah");
        assertThat(updated.licenseClass()).isEqualTo("C");
        assertThat(updated.active()).isFalse();
        assertThat(updated.updatedAt()).isEqualTo(repository.findById(created.id()).orElseThrow().getUpdatedAt());
    }

    @Test
    void reportsMissingDriverAndDeletesPersistedDriver() {
        assertThatThrownBy(() -> service.get(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Driver not found");

        var created = service.create(createRequest("DRV-001", "GH-DL-001"));
        service.delete(created.id());
        assertThat(repository.existsById(created.id())).isFalse();
    }

    private CreateDriverRequest createRequest(String employeeNumber, String licenseNumber) {
        return new CreateDriverRequest(
                employeeNumber,
                "Kwame Boateng",
                "+233 24 123 4567",
                licenseNumber,
                " b ",
                LocalDate.now().plusYears(2)
        );
    }
}
