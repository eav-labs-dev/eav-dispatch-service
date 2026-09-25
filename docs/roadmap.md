# Roadmap

## MVP foundation

- [x] Spring Boot application and health endpoint
- [x] Environment-driven PostgreSQL configuration
- [x] Maven wrapper and GitHub Actions verification
- [x] Containerized local development workflow
- [x] Shipment CRUD and Flyway schema management

## Dispatch workflow

- [x] Driver resources
- [x] Vehicle resources
- [x] Driver and vehicle assignment
- [x] Active-resource double-booking protection
- [x] Controlled shipment lifecycle transitions
- [x] Transition validation and audit history
- [x] Audit-retention safeguards for dispatched shipments

## Quality and release readiness

- [x] Stable API success/error envelope
- [x] Generated OpenAPI documentation and Swagger UI
- [x] PostgreSQL 17 integration tests with Testcontainers
- [x] Concurrent assignment verification
- [x] Expanded validation and conflict coverage
- [x] Non-root production container
- [x] Compose container smoke test
- [x] Liveness/readiness probes and graceful shutdown
- [x] CodeQL security analysis
- [x] Versioned container release workflow
- [x] Complete MVP reviewer demo
- [x] Architecture, deployment, and release documentation
- [x] Integrated MVP workflow on `dev`
- [x] Final MVP documentation reconciliation
- [ ] Promote the verified `dev` release candidate to `main`
- [ ] Create a semantic-version release/tag when the maintainer approves publication

## Post-MVP

- Authentication and role-based authorization
- Multi-tenant organization boundaries
- Route planning and optimization
- Asynchronous domain events and notifications
- Pagination and richer query/filter support
- Operations dashboard
- Managed public deployment

The portfolio MVP stops at a verified, reviewer-friendly service and release path. Post-MVP items should not delay the `dev → main` release.
