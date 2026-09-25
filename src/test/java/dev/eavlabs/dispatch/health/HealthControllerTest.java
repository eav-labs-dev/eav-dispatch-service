package dev.eavlabs.dispatch.health;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the public health response contract without infrastructure dependencies.
 */
class HealthControllerTest {

    @Test
    void returnsStableHealthEnvelope() {
        var response = new HealthController("test").getHealth();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().code()).isEqualTo("HEALTHY");
        assertThat(response.getBody().data().service()).isEqualTo("eav-dispatch-service");
        assertThat(response.getBody().data().status()).isEqualTo("UP");
        assertThat(response.getBody().data().version()).isEqualTo("test");
    }
}
