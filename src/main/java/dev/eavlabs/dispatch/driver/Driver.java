package dev.eavlabs.dispatch.driver;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Persistent driver available for dispatch assignments.
 */
@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String employeeNumber;

    @Column(nullable = false, length = 160)
    private String fullName;

    @Column(nullable = false, length = 32)
    private String phoneNumber;

    @Column(nullable = false, unique = true, length = 64)
    private String licenseNumber;

    @Column(nullable = false, length = 16)
    private String licenseClass;

    private LocalDate licenseExpiresOn;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Driver() {
        // Required by JPA.
    }

    private Driver(
            String employeeNumber,
            String fullName,
            String phoneNumber,
            String licenseNumber,
            String licenseClass,
            LocalDate licenseExpiresOn
    ) {
        this.id = UUID.randomUUID();
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.licenseClass = licenseClass;
        this.licenseExpiresOn = licenseExpiresOn;
        this.active = true;
    }

    /**
     * Creates an active driver with immutable business identifiers.
     *
     * @param employeeNumber normalized employee number
     * @param fullName trimmed full name
     * @param phoneNumber trimmed phone number
     * @param licenseNumber normalized licence number
     * @param licenseClass normalized licence class
     * @param licenseExpiresOn licence expiry date
     * @return new driver aggregate
     */
    public static Driver create(
            String employeeNumber,
            String fullName,
            String phoneNumber,
            String licenseNumber,
            String licenseClass,
            LocalDate licenseExpiresOn
    ) {
        return new Driver(employeeNumber, fullName, phoneNumber, licenseNumber, licenseClass, licenseExpiresOn);
    }

    /**
     * Updates mutable contact, qualification, and availability details.
     *
     * @param fullName trimmed full name
     * @param phoneNumber trimmed phone number
     * @param licenseClass normalized licence class
     * @param licenseExpiresOn licence expiry date
     * @param active whether the driver may receive new assignments
     */
    public void updateDetails(
            String fullName,
            String phoneNumber,
            String licenseClass,
            LocalDate licenseExpiresOn,
            boolean active
    ) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.licenseClass = licenseClass;
        this.licenseExpiresOn = licenseExpiresOn;
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
    public String getEmployeeNumber() { return employeeNumber; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getLicenseClass() { return licenseClass; }
    public LocalDate getLicenseExpiresOn() { return licenseExpiresOn; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
