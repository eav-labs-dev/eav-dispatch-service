# Architecture

## Context

EAV Dispatch is a Spring Boot service for coordinating shipments, drivers, and vehicles. It exposes versioned HTTP resources and stores operational state in PostgreSQL.

The MVP is a modular monolith: one deployable application with explicit domain package boundaries. This keeps local operation and review straightforward while leaving room for later extraction if scale or ownership requires it.

## Request flow

```text
HTTP request
    |
controller + request validation
    |
domain/application service + business rules
    |
Spring Data repository
    |
PostgreSQL schema managed by Flyway
```

Shared response and exception types keep the external API envelope stable without exposing persistence entities directly.

## Implemented domain boundaries

### `shipment`

Owns shipment details, assignment orchestration, lifecycle transitions, and lifecycle history.

The lifecycle is deliberately constrained:

```text
CREATED
  |
  +--> ASSIGNED --> IN_TRANSIT --> DELIVERED
  |
  +--> CANCELLED

ASSIGNED --> CANCELLED
```

Skipped, reversed, and terminal-state transitions are rejected.

Once dispatch begins, hard deletion is blocked so operational history remains available.

### `driver`

Owns driver identity, licence data, active state, and assignment eligibility.

Employee and licence identifiers are normalized and unique.

### `vehicle`

Owns fleet identity, vehicle type, payload information, active state, and assignment eligibility.

Registration numbers are normalized and unique.

## Assignment and concurrency

Assignment requires an eligible active driver, a valid licence, and an active vehicle.

A driver or vehicle may belong to only one shipment in `ASSIGNED` or `IN_TRANSIT`. The repositories combine active-assignment queries with pessimistic row locking so concurrent assignment requests serialize against the shared resources.

Delivery or cancellation releases the resources for later assignment.

## Audit history

Shipment creation, assignment, lifecycle transitions, and cancellation produce chronological persisted history records.

Audit history is stored in PostgreSQL and remains readable after terminal delivery. Dispatched shipments cannot be hard-deleted, preventing routine API operations from erasing their operational record.

## Persistence

PostgreSQL is the source of truth. Hibernate validates mappings but does not create or mutate the production schema.

Flyway migrations are append-only:

- V1: shipments
- V2: drivers
- V3: vehicles
- V4: assignment and lifecycle-history workflow

## Testing strategy

The project uses several verification layers:

- focused validation/service tests;
- H2-backed fast application tests;
- PostgreSQL 17 Testcontainers integration tests;
- complete dispatch workflow verification;
- concurrent assignment verification against PostgreSQL;
- containerized runtime smoke testing.

The PostgreSQL workflow tests verify Flyway migrations, persistence mappings, assignment rules, resource reuse, audit retention, and database-locking behavior against the production database engine.

## Configuration and runtime

Runtime configuration is supplied through environment variables. Secrets are not committed; `.env.example` documents the expected contract.

The production image runs as a non-root user. Spring Boot exposes separate liveness and database-aware readiness probes and handles graceful shutdown.

## Delivery model

GitHub Actions provides:

- Maven verification;
- CodeQL security analysis;
- Compose/container smoke testing;
- release-image build verification;
- semantic-version container publishing only after an explicit release tag.

The MVP does not claim a live public deployment.

## Intentional post-MVP boundaries

Authentication, role-based authorization, multi-tenancy, route optimization, event streaming, pagination, and an operations dashboard are deliberately outside the first portfolio MVP.
