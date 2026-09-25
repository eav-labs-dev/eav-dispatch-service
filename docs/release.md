# Release Process

Dispatch uses reviewed source promotion and immutable container tags. A release does not claim a live deployment.

## Current release candidate

The complete portfolio MVP is integrated on `dev`.

Before promotion to `main`, the release candidate must keep the following checks green:

- Maven CI;
- PostgreSQL workflow verification;
- CodeQL;
- container smoke testing;
- release-image build verification.

The final release PR should also confirm that README, roadmap, changelog, architecture, API contract, reviewer demo, deployment notes, and known limitations describe the integrated code accurately.

## MVP acceptance gate

Mark the release candidate **GO** only when every item below is demonstrated on the integrated `dev` commit.

### Functional behavior

- Shipment create, read, list, update, and controlled delete operations use validated business references.
- Driver and vehicle resources enforce identifier uniqueness and availability rules.
- A shipment can be assigned only to an eligible driver and active vehicle.
- Drivers and vehicles cannot be double-booked while assigned to active shipments.
- Lifecycle changes follow the documented transition graph and reject skipped, reversed, or terminal-state changes.
- Creation, assignment, and status changes produce chronological persisted audit history.
- Dispatched shipments retain their operational history and cannot be hard-deleted.

### Data and API contracts

- Flyway applies V1-V4 to an empty PostgreSQL 17 database.
- Hibernate validates mappings without owning production schema creation.
- PostgreSQL Testcontainers tests cover persistence and the dispatch workflow.
- Concurrent assignment tests exercise database locking against PostgreSQL.
- OpenAPI documents the public endpoints and stable success/error response envelope.
- Malformed input, invalid parameters, missing resources, and data conflicts return controlled status codes without leaking persistence details.

### Runtime and delivery

- `./mvnw verify` passes in CI from the integrated source.
- CodeQL completes without an unresolved release-blocking finding.
- The production image builds and runs as a non-root user.
- The Compose smoke test reaches the public health resource and Actuator readiness probe.
- Runtime configuration is environment-driven and graceful shutdown is enabled.
- Pull-request release checks build the image without publishing it.

### Review evidence

Record these values in the `dev → main` release pull request:

| Evidence | Required value |
| --- | --- |
| Integrated commit | Exact `dev` commit SHA |
| Maven CI | Successful workflow run |
| PostgreSQL workflow | Successful PostgreSQL 17 verification |
| Container smoke test | Successful Compose run |
| CodeQL | Successful analysis |
| Reviewer demo | Documented commands and expected results |
| Schema | Flyway V1-V4 |
| Known limits | Explicit post-MVP items from the roadmap |

Any failed or missing release-blocking item is **NO-GO** until corrected. A live deployment is not required for the portfolio MVP.

## Promote to main

Open a reviewed release pull request from `dev` to `main`.

The release PR should summarize:

- shipment, driver, and vehicle capabilities;
- assignment and lifecycle safeguards;
- audit-history behavior;
- PostgreSQL/Flyway schema;
- test and CI evidence;
- container/release readiness;
- known post-MVP limitations.

Do not merge the release PR until its checks are green.

## Publish a container

After the approved release is on `main`, create a signed semantic-version tag:

```bash
git switch main
git pull --ff-only
git tag -s v0.1.0 -m "EAV Dispatch 0.1.0"
git push origin v0.1.0
```

The release workflow publishes:

- `ghcr.io/eav-labs-dev/eav-dispatch-service:0.1.0`
- `ghcr.io/eav-labs-dev/eav-dispatch-service:0.1`
- an immutable commit-SHA tag

The image includes BuildKit SBOM and provenance attestations.

## Verify and roll back

```bash
docker pull ghcr.io/eav-labs-dev/eav-dispatch-service:0.1.0
docker image inspect ghcr.io/eav-labs-dev/eav-dispatch-service:0.1.0
```

Deployments must inject the variables documented in [Deployment Guide](deployment.md) and use an external PostgreSQL service.

Rollback means redeploying a previously verified immutable version tag. Database migrations remain forward-only.

## Release record

Create a GitHub release from the signed tag and summarize:

- user-visible capabilities;
- schema migrations;
- verification results;
- known MVP limits;
- upgrade or rollback considerations.

Do not claim a public deployment unless a verifiable deployment exists.
