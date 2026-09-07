# EAV Dispatch Service

[![CI](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/eav-labs-dev/eav-dispatch-service/actions/workflows/ci.yml)

Spring Boot microservice for shipments, drivers, vehicles, and dispatch workflows.

## Project Status

The Spring Boot application foundation is under active development on `dev`. The service has reproducible Maven, CI, PostgreSQL, and container foundations; dispatch capabilities are being added in bounded pull requests.

## About

EAV Dispatch Service is a production-style software project under **EAV Labs**, the personal engineering portfolio of Enam/Kwame Avornyo.

The goal is to demonstrate practical engineering through clear documentation, clean structure, real commits, tests, Dockerized development, and deployment-ready thinking.

## Tech Stack

Java, Spring Boot, PostgreSQL, Docker, GitHub Actions, Testcontainers.

## Local Setup

Requirements: Java 17+ and PostgreSQL 16+.

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

## Planned MVP Features

The MVP scope will be built incrementally with real commits and documented progress:

- Shipment CRUD and controlled lifecycle transitions
- Drivers and vehicles
- Driver and vehicle assignment
- Operational audit history
- OpenAPI documentation and PostgreSQL integration testing

Progress and intentional deferrals are tracked in the [Roadmap](docs/roadmap.md).
- Shipment CRUD with validated, unique business references
- Drivers and vehicles
- Driver/vehicle assignment
- Controlled shipment lifecycle transitions
- Audit history
- OpenAPI documentation
- PostgreSQL integration tests

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
