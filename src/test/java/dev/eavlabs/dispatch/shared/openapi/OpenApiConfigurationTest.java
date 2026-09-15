package dev.eavlabs.dispatch.shared.openapi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the generated API document metadata contract.
 */
class OpenApiConfigurationTest {

    @Test
    void describesServiceWithRuntimeVersion() {
        var openApi = new OpenApiConfiguration().dispatchOpenApi("1.2.3");

        assertThat(openApi.getInfo().getTitle()).isEqualTo("EAV Dispatch API");
        assertThat(openApi.getInfo().getVersion()).isEqualTo("1.2.3");
        assertThat(openApi.getInfo().getContact().getName()).isEqualTo("EAV Labs");
        assertThat(openApi.getInfo().getLicense().getName()).isEqualTo("MIT");
    }
}
