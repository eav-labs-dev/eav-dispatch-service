package dev.eavlabs.dispatch.vehicle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistent vehicle available for dispatch assignments.
 */
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String registrationNumber;

    @Column(nullable = false, length = 80)
    private String make;

    @Column(nullable = false, length = 80)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private VehicleType type;

    @Column(nullable = false)
    private int maxPayloadKg;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Vehicle() {
        // Required by JPA.
    }

    private Vehicle(
            String registrationNumber,
            String make,
            String model,
            VehicleType type,
            int maxPayloadKg
    ) {
        this.id = UUID.randomUUID();
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.type = type;
        this.maxPayloadKg = maxPayloadKg;
        this.active = true;
    }

    /**
     * Creates an active vehicle with an immutable registration number.
     *
     * @param registrationNumber normalized registration number
     * @param make vehicle manufacturer
     * @param model vehicle model
     * @param type vehicle category
     * @param maxPayloadKg maximum payload in kilograms
     * @return new vehicle aggregate
     */
    public static Vehicle create(
            String registrationNumber,
            String make,
            String model,
            VehicleType type,
            int maxPayloadKg
    ) {
        return new Vehicle(registrationNumber, make, model, type, maxPayloadKg);
    }

    /**
     * Updates mutable fleet and availability details.
     *
     * @param make vehicle manufacturer
     * @param model vehicle model
     * @param type vehicle category
     * @param maxPayloadKg maximum payload in kilograms
     * @param active whether the vehicle may receive new assignments
     */
    public void updateDetails(
            String make,
            String model,
            VehicleType type,
            int maxPayloadKg,
            boolean active
    ) {
        this.make = make;
        this.model = model;
        this.type = type;
        this.maxPayloadKg = maxPayloadKg;
        this.active = active;
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
    public String getRegistrationNumber() { return registrationNumber; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public VehicleType getType() { return type; }
    public int getMaxPayloadKg() { return maxPayloadKg; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
