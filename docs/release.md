# Release Process

Dispatch uses reviewed source promotion and immutable container tags. A release does not claim a live deployment.

## Preconditions

Before creating a release tag:

1. Merge the approved feature and quality pull requests into `dev`.
2. Confirm Maven CI, PostgreSQL integration tests, CodeQL, and the container smoke test are green.
3. Run the reviewer demo against a clean Compose environment.
4. Reconcile the README, changelog, roadmap, and architecture notes.
5. Merge an approved `dev` to `main` release pull request.

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
