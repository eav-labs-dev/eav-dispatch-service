# Release Process

Dispatch uses reviewed source promotion and immutable container tags. A release does not claim a live deployment.

## Preconditions

Before creating a release tag:

1. Merge the approved feature and quality pull requests into `dev`.
2. Confirm Maven CI, PostgreSQL integration tests, CodeQL, and the container smoke test are green.
3. Run the reviewer demo against a clean Compose environment.
4. Reconcile the README, changelog, roadmap, and architecture notes.
5. Merge an approved `dev` to `main` release pull request.

## MVP acceptance gate

Mark the release candidate **GO** only when every item below is demonstrated on the integrated `dev` commit.

### Functional behavior

- Shipment create, read, list, update, and delete operations use validated business references.
- Driver and vehicle resources enforce identifier uniqueness and assignment availability.
- A shipment can be assigned only to an eligible driver and active vehicle.
- Lifecycle changes follow the documented transition graph and reject skipped, reversed, or terminal-state changes.
- Creation, assignment, and status changes produce chronological, immutable audit history.

### Data and API contracts

- Flyway applies the complete schema history to an empty PostgreSQL 17 database.
- Hibernate validates mappings without creating or changing the production schema.
- PostgreSQL Testcontainers tests cover persistence and the complete dispatch workflow.
- OpenAPI documents the public endpoints and the stable success/error response envelope.
- Malformed input, invalid parameters, missing resources, and data conflicts return documented status codes without leaking persistence details.

### Runtime and delivery

- `./mvnw verify` passes from a clean checkout.
- CodeQL completes without an unresolved release-blocking finding.
- The production image builds and runs as a non-root user.
- The Compose smoke test reaches both the public health resource and the Actuator readiness probe.
- Runtime configuration is environment-driven, credentials stay outside source control, and graceful shutdown is enabled.
- A pull-request run of the release workflow builds the image with publishing disabled.

### Review evidence

Record these links or values in the `dev` to `main` release pull request:

| Evidence | Required value |
| --- | --- |
| Integrated commit | Exact `dev` commit SHA |
| Maven CI | Successful workflow run |
| PostgreSQL workflow | Successful run using PostgreSQL 17 |
| Container smoke test | Successful Compose run |
| CodeQL | Successful analysis run |
| Reviewer demo | Commands used and observed result |
| Schema | Flyway versions included in the release |
| Known limits | Explicit post-MVP items from the roadmap |

Any failed or missing item is **NO-GO** until corrected or explicitly removed from the MVP scope in a reviewed change. A live deployment is not required for the portfolio MVP, and must not be claimed without a verifiable deployment URL.

## Integration discipline

Integrate independent foundation, reliability, documentation, and delivery changes before stacked domain branches. Merge driver management before vehicle management, then retarget the assignment workflow to `dev` and verify that its diff contains only assignment, lifecycle, audit, and related documentation. Retarget workflow-level PostgreSQL tests and the reviewer demo only after the assignment workflow lands.

After each integration step:

1. Update the next dependent branch from `dev` without rewriting reviewed history.
2. Recheck the pull-request diff for duplicated migrations or changelog entries.
3. Run all checks required by that branch.
4. Stop the sequence if Flyway versions, API contracts, or lifecycle rules conflict.

## Publish a container

Create a signed semantic-version tag from the verified commit on `main`:

```bash
git switch main
git pull --ff-only
git tag -s v0.1.0 -m "EAV Dispatch 0.1.0"
git push origin v0.1.0
```

The release workflow builds the repository Dockerfile and publishes the image to GitHub Container Registry. A `v0.1.0` tag produces:

- `ghcr.io/eav-labs-dev/eav-dispatch-service:0.1.0`
- `ghcr.io/eav-labs-dev/eav-dispatch-service:0.1`
- an immutable commit-SHA tag

The image includes BuildKit SBOM and provenance attestations. Pull requests that change the release workflow or Dockerfile build the image without publishing it.

## Verify the published artifact

```bash
docker pull ghcr.io/eav-labs-dev/eav-dispatch-service:0.1.0
docker image inspect ghcr.io/eav-labs-dev/eav-dispatch-service:0.1.0
```

Deployments must inject the runtime variables documented in [Deployment Guide](deployment.md) and use a managed PostgreSQL database. Rollback means redeploying a previously verified immutable version tag; database migrations remain forward-only.

## Release record

Create a GitHub release from the signed tag and summarize:

- user-visible capabilities;
- schema migrations;
- verification results;
- known MVP limits;
- upgrade or rollback considerations.

Do not publish from an unreviewed branch, reuse an existing version tag, or place credentials in workflow files.
