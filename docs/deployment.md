# Deployment Guide

## Local container deployment

Requirements: Docker Engine with Docker Compose v2.

```bash
docker compose up --build
```

The API listens on `http://localhost:8080`; PostgreSQL listens on `localhost:5432`. Confirm application health with:

```bash
curl --fail http://localhost:8080/api/v1/health
```

Stop the containers without deleting the database volume:

```bash
docker compose down
```

Delete local database data only when a clean environment is intentionally required:

```bash
docker compose down --volumes
```

## Runtime contract

The service requires these environment variables:

| Variable | Purpose |
| --- | --- |
| `DATABASE_URL` | PostgreSQL JDBC URL |
| `DATABASE_USERNAME` | Database role |
| `DATABASE_PASSWORD` | Database credential |
| `PORT` | HTTP port; defaults to `8080` |
| `APP_VERSION` | Version reported by the health resource |

A deployment platform should inject credentials through its secret store, run one application instance for the initial MVP, and provide a managed PostgreSQL database with backups. Health monitoring can call `/actuator/health`; the public reviewer endpoint is `/api/v1/health`.

## Release gate

Before promoting `dev` to `main`:

1. Run `./mvnw verify` from a clean checkout.
2. Build the container with `docker build -t eav-dispatch-service .`.
3. Start the Compose stack and verify the health endpoint.
4. Review new Flyway migrations against a disposable PostgreSQL database.
5. Confirm GitHub Actions is green on the release pull request.

This repository does not currently claim a live production deployment.

