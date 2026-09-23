# EAV Dispatch Service

[![CI](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/ci.yml)

Spring Boot microservice for shipments, drivers, vehicles, and dispatch workflows.

## Project Status

Development is on `dev`; `main` has not yet received the MVP release. Shipment CRUD, request validation, normalized business references, PostgreSQL persistence, and the initial Flyway migration are implemented. Drivers, vehicles, assignment, lifecycle transitions, and audit history remain planned.

CI runs Maven verification. Existing application tests use H2 in PostgreSQL compatibility mode; PostgreSQL Testcontainers coverage is still pending.

## About

EAV Dispatch Service is a production-style software project under **EAV Labs**, the personal engineering portfolio of Enam/Kwame Avornyo.

The goal is to demonstrate practical engineering through clear documentation, clean structure, real commits, tests, Dockerized development, and deployment-ready thinking.

## Tech Stack

Java 17, Spring Boot 4.1.1, PostgreSQL, Flyway, Docker, and GitHub Actions. Testcontainers dependencies are present; database container tests are not yet implemented.

## Local Setup

Requirements: Java 17 and PostgreSQL. The Compose environment uses PostgreSQL 17.

Start PostgreSQL and provision the database and role from `.env.example` before running the application. For a local development database using the supplied credentials:

```bash
docker compose up -d postgres
```

Then, from the repository root:

```bash
cp .env.example .env
set -a && . ./.env && set +a
./mvnw spring-boot:run
```

Verify the service at `GET http://localhost:8080/api/v1/health`.

Run the same verification used in CI with:

```bash
./mvnw verify
```

For a complete containerized environment instead:

```bash
docker compose up --build
curl --fail http://localhost:8080/api/v1/health
```

See [Deployment Guide](docs/deployment.md) for lifecycle and release-gate commands.

## MVP progress

| Capability | Status |
| --- | --- |
| Shipment create, list, retrieve, update, and delete | Implemented |
| Request validation and normalized unique references | Implemented |
| PostgreSQL schema, Flyway, Docker, and Maven CI | Implemented |
| Drivers, vehicles, and assignment | Planned |
| Controlled lifecycle transitions and audit history | Planned |
| OpenAPI and PostgreSQL Testcontainers tests | Planned |

Follow the [Shipment Reviewer Guide](docs/reviewer-guide.md) for a CRUD walkthrough and expected responses.

Progress and intentional deferrals are tracked in the [Roadmap](docs/roadmap.md).

## Repository Standard

This repo will include:

- Clear README
- Setup instructions
- Environment example
- Tests
- CI workflow
- Documentation
- Roadmap
- Docker support where applicable

## What This Project Demonstrates

Enterprise backend engineering, Java/Spring Boot, microservice design, integration testing, and logistics workflow modelling.

See [Architecture](docs/architecture.md) for the modular boundaries, persistence ownership, and delivery model.
