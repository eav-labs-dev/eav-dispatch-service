package dev.eavlabs.dispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import dev.eavlabs.dispatch.shared.http.HttpProtectionProperties;

/**
 * Boots the EAV Dispatch Service application.
 */
@SpringBootApplication
@EnableConfigurationProperties(HttpProtectionProperties.class)
public class EavDispatchServiceApplication {

    /**
     * Starts the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EavDispatchServiceApplication.class, args);
    }
}
