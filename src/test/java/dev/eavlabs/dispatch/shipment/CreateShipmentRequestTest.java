package dev.eavlabs.dispatch.shipment;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies request boundary constraints.
 */
class CreateShipmentRequestTest {

    @Test
    void rejectsBlankFieldsAndPastPickup() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var request = new CreateShipmentRequest(
                    " ",
                    " ",
                    " ",
                    " ",
                    OffsetDateTime.now().minusDays(1)
            );

            assertThat(validator.validate(request)).hasSize(5);
        }
    }
}
