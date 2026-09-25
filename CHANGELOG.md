# Changelog

## Unreleased

### Added

- Spring Boot service foundation with public health endpoint and environment-driven configuration.
- Shipment CRUD with validated and normalized business references.
- Driver CRUD with normalized employee/licence identifiers, licence validation, and availability state.
- Vehicle CRUD with normalized registration numbers, fleet availability, vehicle type, and payload limits.
- Driver/vehicle assignment with eligibility checks and active-resource double-booking protection.
- Controlled shipment lifecycle transitions with terminal-state protection.
- Persisted lifecycle audit history and hard-deletion safeguards after dispatch begins.
- PostgreSQL persistence with Flyway migrations V1-V4.
- Generated OpenAPI 3 documentation and Swagger UI.
- PostgreSQL 17 Testcontainers persistence and full-workflow verification.
- Concurrent PostgreSQL assignment verification.
- Non-root multi-stage Docker image and PostgreSQL Compose environment.
- Container smoke testing, liveness/readiness probes, and graceful shutdown.
- CodeQL security analysis.
- Versioned GHCR release workflow with SBOM and provenance metadata.
- Reviewer guides, full MVP demo, architecture notes, deployment guidance, and release gate.

### Changed

- Normalized malformed JSON, invalid parameter, validation, not-found, and data-conflict responses through the stable API envelope.
- Protected active driver/vehicle assignment with database locking.
- Release resources for reuse after delivery or cancellation.
- Reconciled project documentation to reflect the integrated Dispatch MVP.

## 0.1.0 - Portfolio Rebuild Started

- Reset repository for EAV Labs portfolio rebuild.
- Added initial README and documentation structure.
