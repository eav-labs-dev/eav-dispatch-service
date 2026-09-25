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
| `SHUTDOWN_TIMEOUT` | Maximum time for in-flight requests to finish during shutdown; defaults to `20s` |

A deployment platform should inject credentials through its secret store, run one application instance for the initial MVP, and provide a managed PostgreSQL database with backups. Forwarded headers are interpreted through Spring's framework strategy so generated URLs and request metadata remain correct behind a trusted proxy.

Use the Actuator probe groups for orchestration:

| Probe | Endpoint | Meaning |
| --- | --- | --- |
| Liveness | `/actuator/health/liveness` | The application process can continue running |
| Readiness | `/actuator/health/readiness` | The application and PostgreSQL dependency can receive traffic |

Health details are not exposed. The public reviewer endpoint remains `/api/v1/health`, while deployment traffic should use the readiness probe. The service handles `SIGTERM` with graceful shutdown and gives in-flight requests up to `SHUTDOWN_TIMEOUT` to finish.

## Release gate

Before promoting `dev` to `main`:

1. Run `./mvnw verify` from a clean checkout.
2. Build the container with `docker build -t eav-dispatch-service .`.
3. Start the Compose stack and verify the health endpoint.
4. Review new Flyway migrations against a disposable PostgreSQL database.
5. Confirm GitHub Actions is green on the release pull request.

This repository does not currently claim a live production deployment.
