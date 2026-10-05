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
| `SERVER_CONNECTION_TIMEOUT` | Time allowed to establish/read the request connection; defaults to `5s` |
| `DISPATCH_RATE_LIMIT_REQUESTS_PER_MINUTE` | Per-client application limit; defaults to `120` |
| `DISPATCH_MAX_REQUEST_BODY_BYTES` | Maximum declared API request body; defaults to `1048576` (1 MiB) |

A deployment platform should inject credentials through its secret store, run one application instance for the initial MVP, and provide a managed PostgreSQL database with backups. Forwarded headers are interpreted through Spring's framework strategy so generated URLs and request metadata remain correct behind a trusted proxy.

Use the Actuator probe groups for orchestration:

| Probe | Endpoint | Meaning |
| --- | --- | --- |
| Liveness | `/actuator/health/liveness` | The application process can continue running |
| Readiness | `/actuator/health/readiness` | The application and PostgreSQL dependency can receive traffic |

Health details are not exposed. The public reviewer endpoint remains `/api/v1/health`, while deployment traffic should use the readiness probe. The service handles `SIGTERM` with graceful shutdown and gives in-flight requests up to `SHUTDOWN_TIMEOUT` to finish.

The application enforces a per-client fixed-window limit for business API routes and emits a
stable JSON `429` response. Public and Actuator health routes are exempt. Caddy should still
apply connection, header, and body controls at the edge; application throttling is the final
service-level guard. The current limiter is process-local and is appropriate for the single
MVP instance. Use a shared Redis-backed limiter before horizontally scaling.

The application emits API-safe `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`,
and `Permissions-Policy` headers on every response. Configure HSTS at Caddy, where HTTPS
terminates, so local HTTP development and direct container health checks remain usable.
After each OCI deployment, the workflow checks the public readiness endpoint and fails unless
all four defensive headers survive the complete HTTPS/Caddy path.

## Release gate

Before promoting `dev` to `main`:

1. Run `./mvnw verify` from a clean checkout.
2. Build the container with `docker build -t eav-dispatch-service .`.
3. Start the Compose stack and verify the health endpoint.
4. Review new Flyway migrations against a disposable PostgreSQL database.
5. Confirm GitHub Actions is green on the release pull request.

This repository does not currently claim a live production deployment.
