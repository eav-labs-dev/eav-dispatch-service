# Reviewer guide

EAV Dispatch is a portfolio logistics API demonstrating shipment, driver, vehicle, assignment, lifecycle, audit, persistence, testing, and delivery workflows.

## Fast review path

From the repository root:

```bash
cp .env.example .env
docker compose up --build --detach
until curl --fail --silent http://localhost:8080/actuator/health; do sleep 2; done
curl --fail http://localhost:8080/api/v1/health
```

Then inspect:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Run the complete business workflow from [Dispatch MVP reviewer demo](mvp-demo.md).

## What the demo proves

The walkthrough creates a driver, vehicle, and shipments, then demonstrates:

- validated resource creation;
- driver/vehicle assignment;
- rejection of double-booking;
- controlled shipment transitions;
- release of resources after delivery;
- chronological lifecycle history;
- terminal-state protection;
- audit retention after rejected deletion.

## Automated verification

Run:

```bash
./mvnw verify
```

CI adds PostgreSQL 17 Testcontainers verification, concurrent assignment coverage, CodeQL, and a Compose container smoke test.

## API contract

See [API Contract](api-contract.md) for the endpoint map and response-envelope behavior.

## MVP boundaries

The current MVP intentionally has no authentication or role-based authorization and should not be exposed publicly without an access-control layer.

Pagination, route optimization, multi-tenancy, asynchronous notifications, and a public managed deployment are also deferred.

No live production deployment is claimed.
