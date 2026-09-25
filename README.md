# EAV Dispatch Service

[![CI](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/ci.yml)
[![Security](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/security.yml/badge.svg?branch=dev)](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/security.yml)

Spring Boot logistics API for shipments, drivers, vehicles, assignments, controlled delivery lifecycles, and auditable dispatch workflows.

## Status

The portfolio MVP is feature-complete on `dev` and is being prepared for promotion to `main`.

The integrated service includes shipment, driver, and vehicle management; guarded driver/vehicle assignment; controlled shipment transitions; immutable lifecycle history; PostgreSQL/Flyway persistence; OpenAPI; Testcontainers; Docker/Compose; CI; container smoke testing; CodeQL; and a versioned container-release workflow.

There is no claimed public production deployment.

## Core capabilities

| Capability | Status |
| --- | --- |
| Shipment CRUD with validated business references | Implemented |
| Driver management and licence validation | Implemented |
| Vehicle management and fleet availability | Implemented |
| Driver/vehicle assignment | Implemented |
| Active-resource double-booking protection | Implemented |
| Controlled shipment lifecycle transitions | Implemented |
| Lifecycle audit history and retention safeguards | Implemented |
| PostgreSQL persistence with Flyway V1-V4 | Implemented |
| Generated OpenAPI and Swagger UI | Implemented |
| PostgreSQL Testcontainers workflow verification | Implemented |
| Docker/Compose runtime | Implemented |
| CI, CodeQL, container smoke test, and release pipeline | Implemented |

## Tech stack

- Java 17
- Spring Boot 4.1.1
- Spring MVC and Spring Data JPA
- PostgreSQL 17
- Flyway
- springdoc OpenAPI / Swagger UI
- Testcontainers and H2 for automated verification
- Docker and Docker Compose
- GitHub Actions and CodeQL

## Architecture

EAV Dispatch is a modular monolith. The service keeps shipment workflow, driver resources, vehicle resources, persistence, and HTTP concerns in explicit package boundaries while remaining one deployable application.

PostgreSQL is the source of truth. Flyway owns schema changes, Hibernate validates mappings, and assignment checks use database locking plus active-assignment queries to prevent resource double-booking.

See [Architecture](docs/architecture.md) for the design and persistence model.

## Local setup

Requirements:

- Java 17
- Docker Engine with Docker Compose v2

Start the full stack:

```bash
cp .env.example .env
docker compose up --build
```

Verify the public health endpoint:

```bash
curl --fail http://localhost:8080/api/v1/health
```

Run Maven verification:

```bash
./mvnw verify
```

Generated API documentation is available while the service is running:

- OpenAPI JSON: `/v3/api-docs`
- OpenAPI YAML: `/v3/api-docs.yaml`
- Swagger UI: `/swagger-ui.html`

## Reviewer workflow

For the complete portfolio walkthrough, use [Dispatch MVP reviewer demo](docs/mvp-demo.md).

It demonstrates:

1. creating a driver, vehicle, and shipments;
2. assigning eligible resources;
3. rejecting active-resource double booking;
4. moving a shipment through `ASSIGNED → IN_TRANSIT → DELIVERED`;
5. releasing resources after delivery;
6. reading chronological audit history;
7. rejecting terminal-state mutation and hard deletion.

The concise API surface is documented in [API Contract](docs/api-contract.md).

## Verification and delivery

The repository verifies the MVP through:

- Maven CI on integration and release branches;
- PostgreSQL 17 Testcontainers coverage, including concurrent assignment behavior;
- a Compose container smoke test;
- CodeQL security analysis;
- a non-root production image;
- liveness/readiness probes and graceful shutdown;
- a release workflow that builds on pull requests and publishes only after a deliberate semantic-version tag.

See [Deployment Guide](docs/deployment.md) and [Release Process](docs/release.md).

## Known MVP limits

The following are intentionally post-MVP:

- authentication and role-based authorization;
- multi-tenant organization boundaries;
- route planning and optimization;
- asynchronous domain events and notifications;
- pagination for larger collections;
- an operations dashboard;
- public production deployment.

These limits are tracked in the [Roadmap](docs/roadmap.md).

## EAV Labs

EAV Dispatch is one of the EAV Labs portfolio projects demonstrating practical backend engineering, API design, business workflow modelling, database migrations, automated testing, containerized delivery, CI/CD, and release discipline.
