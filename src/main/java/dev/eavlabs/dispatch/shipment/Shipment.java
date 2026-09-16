package dev.eavlabs.dispatch.shipment;

import dev.eavlabs.dispatch.driver.Driver;
import dev.eavlabs.dispatch.shared.error.ResourceConflictException;
import dev.eavlabs.dispatch.vehicle.Vehicle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Persistent shipment aggregate.
 */
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String reference;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 160)
    private String origin;

    @Column(nullable = false, length = 160)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ShipmentStatus status;

    private OffsetDateTime scheduledPickupAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Shipment() {
        // Required by JPA.
    }

    private Shipment(
            String reference,
            String description,
            String origin,
            String destination,
            OffsetDateTime scheduledPickupAt
    ) {
        this.id = UUID.randomUUID();
        this.reference = reference;
        this.description = description;
        this.origin = origin;
        this.destination = destination;
        this.status = ShipmentStatus.CREATED;
        this.scheduledPickupAt = scheduledPickupAt;
    }

    /**
     * Creates a shipment in the initial lifecycle state.
     */
    public static Shipment create(
            String reference,
            String description,
            String origin,
            String destination,
            OffsetDateTime scheduledPickupAt
    ) {
        return new Shipment(reference, description, origin, destination, scheduledPickupAt);
    }

    /**
     * Updates editable shipment details without changing lifecycle state.
     */
    public void updateDetails(
            String description,
            String origin,
            String destination,
            OffsetDateTime scheduledPickupAt
    ) {
        this.description = description;
        this.origin = origin;
        this.destination = destination;
        this.scheduledPickupAt = scheduledPickupAt;
    }

    /**
     * Assigns dispatch resources and advances a created shipment.
     *
     * @param driver active driver
     * @param vehicle active vehicle
     */
    public void assign(Driver driver, Vehicle vehicle) {
        requireStatus(ShipmentStatus.CREATED, "Only created shipments can be assigned");
        this.driver = driver;
        this.vehicle = vehicle;
        this.status = ShipmentStatus.ASSIGNED;
    }

    /**
     * Applies a permitted operational status transition.
     *
     * @param targetStatus requested next status
     */
    public void transitionTo(ShipmentStatus targetStatus) {
        var allowed = switch (status) {
            case CREATED -> targetStatus == ShipmentStatus.CANCELLED;
            case ASSIGNED -> targetStatus == ShipmentStatus.IN_TRANSIT
                    || targetStatus == ShipmentStatus.CANCELLED;
            case IN_TRANSIT -> targetStatus == ShipmentStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
        if (!allowed) {
            throw new ResourceConflictException(
                    "Shipment cannot transition from " + status + " to " + targetStatus
            );
        }
        this.status = targetStatus;
    }

    private void requireStatus(ShipmentStatus expected, String message) {
        if (status != expected) {
            throw new ResourceConflictException(message);
        }
    }

    @PrePersist
    void onCreate() {
        var now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getReference() { return reference; }
    public String getDescription() { return description; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public ShipmentStatus getStatus() { return status; }
    public OffsetDateTime getScheduledPickupAt() { return scheduledPickupAt; }
    public Driver getDriver() { return driver; }
    public Vehicle getVehicle() { return vehicle; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
