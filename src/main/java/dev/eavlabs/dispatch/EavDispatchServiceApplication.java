package dev.eavlabs.dispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Boots the EAV Dispatch Service application.
 */
@SpringBootApplication
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
