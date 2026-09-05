# EAV Dispatch Service

Spring Boot microservice for shipments, drivers, vehicles, and dispatch workflows.

## Project Status

The Spring Boot application foundation is under active development on `dev`.

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

Run the test suite with:

```bash
./mvnw verify
```

## Planned MVP Features

The MVP scope will be built incrementally with real commits and documented progress.

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
