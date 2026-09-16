package dev.eavlabs.dispatch.shipment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable audit record for a shipment lifecycle change.
 */
@Entity
@Table(name = "shipment_status_history")
public class ShipmentStatusHistory {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private ShipmentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ShipmentStatus newStatus;

    @Column(length = 500)
    private String note;

    @Column(nullable = false, updatable = false)
    private Instant changedAt;

    protected ShipmentStatusHistory() {
        // Required by JPA.
    }

    private ShipmentStatusHistory(
            Shipment shipment,
            ShipmentStatus previousStatus,
            ShipmentStatus newStatus,
            String note
    ) {
        this.id = UUID.randomUUID();
        this.shipment = shipment;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.note = normalizeNote(note);
    }

    /**
     * @param shipment affected shipment
     * @param previousStatus status before the change, or null for creation
     * @param newStatus status after the change
     * @param note optional context
     * @return immutable audit entry
     */
    public static ShipmentStatusHistory record(
            Shipment shipment,
            ShipmentStatus previousStatus,
            ShipmentStatus newStatus,
            String note
    ) {
        return new ShipmentStatusHistory(shipment, previousStatus, newStatus, note);
    }

    @PrePersist
    void onCreate() {
        this.changedAt = Instant.now();
    }

    private String normalizeNote(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public UUID getId() { return id; }
    public Shipment getShipment() { return shipment; }
    public ShipmentStatus getPreviousStatus() { return previousStatus; }
    public ShipmentStatus getNewStatus() { return newStatus; }
    public String getNote() { return note; }
    public Instant getChangedAt() { return changedAt; }
}
