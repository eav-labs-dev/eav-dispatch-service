package dev.eavlabs.dispatch.shipment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
