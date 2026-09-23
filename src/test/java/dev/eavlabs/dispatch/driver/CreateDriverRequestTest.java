package dev.eavlabs.dispatch.driver;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies driver request boundary constraints.
 */
class CreateDriverRequestTest {

    @Test
    void rejectsBlankIdentityInvalidPhoneAndExpiredLicence() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var request = new CreateDriverRequest(
                    " ", " ", "invalid", " ", " ", LocalDate.now().minusDays(1)
            );

            assertThat(validator.validate(request)).hasSize(6);
        }
    }
}
