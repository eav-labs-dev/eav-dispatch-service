package dev.eavlabs.dispatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies Flyway and Hibernate against the same PostgreSQL major version used by Compose.
 */
@Testcontainers
@SpringBootTest
class PostgreSqlPersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void appliesShipmentMigrationUsingPostgreSqlTypes() {
        var serverVersion = jdbcTemplate.queryForObject(
                "SELECT current_setting('server_version_num')::integer",
                Integer.class
        );
        var shipmentTableExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM information_schema.tables "
                        + "WHERE table_schema = 'public' AND table_name = 'shipments')",
                Boolean.class
        );
        var pickupColumnType = jdbcTemplate.queryForObject(
                "SELECT data_type FROM information_schema.columns "
                        + "WHERE table_schema = 'public' AND table_name = 'shipments' "
                        + "AND column_name = 'scheduled_pickup_at'",
                String.class
        );
        var appliedMigrations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true",
                Integer.class
        );

        assertThat(serverVersion).isGreaterThanOrEqualTo(170000);
        assertThat(shipmentTableExists).isTrue();
        assertThat(pickupColumnType).isEqualTo("timestamp with time zone");
        assertThat(appliedMigrations).isEqualTo(1);
    }
}
