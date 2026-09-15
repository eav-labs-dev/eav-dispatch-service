# Architecture

## Context

EAV Dispatch is a Spring Boot service for coordinating shipments, drivers, and vehicles. It exposes versioned HTTP resources and stores operational state in PostgreSQL.

The MVP is intentionally a modular monolith. Shipment, driver, vehicle, assignment, and audit capabilities live in one deployable service while retaining explicit package boundaries. This keeps local operation and review straightforward without preventing later extraction when scale or ownership requires it.

## Application shape

```text
HTTP request
    |
controller and request validation
    |
application service and business rules
    |
Spring Data repository
    |
PostgreSQL schema managed by Flyway
```

Shared API response and exception-handling types provide a stable external envelope without coupling domain objects directly to HTTP responses.

## Data ownership

PostgreSQL is the source of truth. Hibernate validates mappings against the schema but does not create or mutate production tables. Flyway migrations are append-only and run during application startup.

The test profile uses H2 for fast foundation and service tests. A separate Testcontainers integration test starts PostgreSQL 17, applies Flyway migrations, lets Hibernate validate the mappings, and checks PostgreSQL-specific column semantics. This keeps the fast feedback loop while exercising the production database engine in CI.

## Configuration

Runtime configuration is supplied through environment variables. Secrets are never committed; `.env.example` documents local-only defaults. Docker Compose uses service-network values explicitly and does not read an untracked developer `.env` file.

## Delivery model

GitHub Actions executes the Maven verification lifecycle on pushes to `dev` and `main` and on pull requests. The multi-stage Dockerfile builds the executable jar and runs it as a non-root user. `compose.yml` provides a reproducible application-and-database environment for local review.

## Planned boundaries

- `shipment`: shipment details and controlled lifecycle transitions.
- `driver`: driver identity and availability.
- `vehicle`: fleet identity and capacity.
- `assignment`: shipment-to-driver/vehicle allocation rules.
- `audit`: immutable history for operational state changes.

Authentication, multi-tenancy, event streaming, route optimization, and a web dashboard are deliberately outside the first MVP.
