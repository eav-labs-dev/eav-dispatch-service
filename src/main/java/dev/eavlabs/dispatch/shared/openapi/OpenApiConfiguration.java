package dev.eavlabs.dispatch.shared.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defines stable service metadata for generated OpenAPI documents.
 */
@Configuration
public class OpenApiConfiguration {

    /**
     * Builds the public API description shown in Swagger UI.
     *
     * @param applicationVersion version supplied through runtime configuration
     * @return configured OpenAPI model
     */
    @Bean
    public OpenAPI dispatchOpenApi(@Value("${info.app.version}") String applicationVersion) {
        return new OpenAPI().info(new Info()
                .title("EAV Dispatch API")
                .version(applicationVersion)
                .description("Shipment, fleet, assignment, and delivery lifecycle API")
                .contact(new Contact()
                        .name("EAV Labs")
                        .url("https://github.com/eav-labs-dev"))
                .license(new License()
                        .name("MIT")
                        .url("https://opensource.org/license/mit")));
    }
}
