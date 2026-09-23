package dev.eavlabs.dispatch.driver;

import dev.eavlabs.dispatch.shared.error.ResourceConflictException;
import dev.eavlabs.dispatch.shared.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Coordinates driver CRUD rules and transactions.
 */
@Service
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository repository;

    /**
     * @param repository driver persistence boundary
     */
    public DriverService(DriverRepository repository) {
        this.repository = repository;
    }

    /**
     * @param request validated driver details
     * @return persisted driver
     */
    @Transactional
    public DriverResponse create(CreateDriverRequest request) {
        var employeeNumber = normalize(request.employeeNumber());
        var licenseNumber = normalize(request.licenseNumber());
        rejectDuplicateIdentifiers(employeeNumber, licenseNumber);

        var driver = Driver.create(
                employeeNumber,
                request.fullName().trim(),
                request.phoneNumber().trim(),
                licenseNumber,
                normalize(request.licenseClass()),
                request.licenseExpiresOn()
        );
        return DriverResponse.from(repository.save(driver));
    }

    /**
     * @param id driver identifier
     * @return requested driver
     */
    public DriverResponse get(UUID id) {
        return DriverResponse.from(find(id));
    }

    /**
     * @return all drivers
     */
    public List<DriverResponse> list() {
        return repository.findAll().stream().map(DriverResponse::from).toList();
    }

    /**
     * @param id driver identifier
     * @param request validated editable details
     * @return updated driver
     */
    @Transactional
    public DriverResponse update(UUID id, UpdateDriverRequest request) {
        var driver = find(id);
        driver.updateDetails(
                request.fullName().trim(),
                request.phoneNumber().trim(),
                normalize(request.licenseClass()),
                request.licenseExpiresOn(),
                request.active()
        );
        repository.flush();
        return DriverResponse.from(driver);
    }

    /**
     * @param id driver identifier
     */
    @Transactional
    public void delete(UUID id) {
        repository.delete(find(id));
    }

    private Driver find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
    }

    private void rejectDuplicateIdentifiers(String employeeNumber, String licenseNumber) {
        if (repository.existsByEmployeeNumber(employeeNumber)) {
            throw new ResourceConflictException("Driver employee number already exists");
        }
        if (repository.existsByLicenseNumber(licenseNumber)) {
            throw new ResourceConflictException("Driver licence number already exists");
        }
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
