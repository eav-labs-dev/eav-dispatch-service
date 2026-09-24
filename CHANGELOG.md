# Changelog

## Unreleased

### Added

- Vehicle CRUD with normalized unique registrations, fleet availability, payload limits, and Flyway persistence.
- Added PostgreSQL 17 Testcontainers verification for Flyway and production column semantics.
- Added generated OpenAPI 3 documentation and Swagger UI with runtime version metadata.
- Added driver CRUD with normalized employee/licence identifiers, availability state, validation, and Flyway persistence.
- Fixed shipment update responses to include the timestamp refreshed by persistence callbacks.

- Added the Spring Boot application foundation and public health endpoint.
- Added PostgreSQL, Flyway, validation, Actuator, and Testcontainers dependencies.
- Added environment-driven configuration and baseline tests.
- Added Maven-wrapper verification and build checks in GitHub Actions.
- Added validated shipment CRUD, Flyway schema migration, and normalized API errors.
- Added a non-root multi-stage container image, PostgreSQL Compose environment, and delivery documentation.

## 0.1.0 - Portfolio Rebuild Started

- Reset repository for EAV Labs portfolio rebuild.
- Added initial README and documentation structure.
