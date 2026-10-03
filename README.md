# EAV Dispatch Service

[![CI](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/ci.yml)
[![Security](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/security.yml/badge.svg?branch=main)](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/security.yml)
[![Deploy](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/deploy-oci.yml/badge.svg?branch=main)](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/deploy-oci.yml)

Spring Boot logistics API for shipments, drivers, vehicles, assignments, controlled delivery lifecycles, and auditable dispatch workflows.

## Status

The portfolio MVP is deployed from `main` to an Oracle Cloud Infrastructure ARM64 VM through GitHub Actions.

The production stack runs EAV Dispatch and PostgreSQL with Docker Compose, keeps PostgreSQL private on the Docker network, exposes the API only through Caddy, and serves HTTPS at [dispatch.env.pm](https://dispatch.env.pm).

- Live API: [https://dispatch.env.pm](https://dispatch.env.pm)
- Swagger UI: [https://dispatch.env.pm/swagger-ui.html](https://dispatch.env.pm/swagger-ui.html)
- OpenAPI JSON: [https://dispatch.env.pm/v3/api-docs](https://dispatch.env.pm/v3/api-docs)
- Readiness: [https://dispatch.env.pm/actuator/health/readiness](https://dispatch.env.pm/actuator/health/readiness)

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
| Per-client API throttling and request-size protection | Implemented |

## Tech stack

- Java 17
- Spring Boot 4.1.1
- Spring MVC and Spring Data JPA
- PostgreSQL 17
- Flyway
- springdoc OpenAPI / Swagger UI
- Testcontainers and H2 for automated verification
- Docker and Docker Compose
- Oracle Cloud Infrastructure (ARM64)
- Caddy reverse proxy and automatic HTTPS
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

Generated API documentation is available locally and in production:

- Swagger UI: [https://dispatch.env.pm/swagger-ui.html](https://dispatch.env.pm/swagger-ui.html)
- OpenAPI JSON: [https://dispatch.env.pm/v3/api-docs](https://dispatch.env.pm/v3/api-docs)
- OpenAPI YAML: [https://dispatch.env.pm/v3/api-docs.yaml](https://dispatch.env.pm/v3/api-docs.yaml)

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

## HTTP safeguards

Business endpoints under `/api/` are limited per client address. The public
`/api/v1/health` endpoint and Actuator probes are exempt so deployment health checks remain
reliable. Limit breaches return HTTP `429` in the stable API envelope with `Retry-After` and
rate-limit headers; declared request bodies above the configured maximum return HTTP `413`.

The defaults are `120` requests per minute and `1 MiB` per request. Configure them with
`DISPATCH_RATE_LIMIT_REQUESTS_PER_MINUTE` and `DISPATCH_MAX_REQUEST_BODY_BYTES`.

## Verification and delivery

The repository verifies the MVP through:

- Maven CI on integration and release branches;
- PostgreSQL 17 Testcontainers coverage, including concurrent assignment behavior;
- a Compose container smoke test;
- CodeQL security analysis;
- a non-root production image;
- liveness/readiness probes and graceful shutdown;
- an OCI deployment workflow that deploys `main` after CI succeeds and verifies local and public readiness;
- a release workflow that publishes semantic-versioned container images.

See [Deployment Guide](docs/deployment.md) and [Release Process](docs/release.md).

## Known MVP limits

The following are intentionally post-MVP:

- authentication and role-based authorization;
- multi-tenant organization boundaries;
- route planning and optimization;
- asynchronous domain events and notifications;
- pagination for larger collections;
- an operations dashboard.

These limits are tracked in the [Roadmap](docs/roadmap.md).

## EAV Labs

EAV Dispatch is one of the EAV Labs portfolio projects demonstrating practical backend engineering, API design, business workflow modelling, database migrations, automated testing, containerized delivery, CI/CD, and release discipline.
