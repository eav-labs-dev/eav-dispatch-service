# Roadmap

## MVP foundation

- [x] Spring Boot application and health endpoint
- [x] Environment-driven PostgreSQL configuration
- [x] Maven wrapper and GitHub Actions verification
- [x] Containerized local development workflow
- [x] Shipment CRUD and schema migration

## Dispatch workflow

- [ ] Driver resources
- [ ] Vehicle resources
- [ ] Driver and vehicle assignment
- [ ] Controlled shipment lifecycle transitions
- [ ] Transition validation and audit history

## Quality and release readiness

- [x] Shipment CRUD reviewer walkthrough
- [ ] OpenAPI documentation
- [ ] PostgreSQL integration tests with Testcontainers
- [ ] Expanded validation and conflict coverage
- [ ] Authorization coverage when authentication is introduced
- [ ] MVP demo workflow and final architecture review
- [ ] Deployment configuration verification

## Delivery sequence

1. Add driver and vehicle resources in independent branches from `dev`.
2. Build assignment once both resource contracts are available.
3. Implement controlled transitions with audit history and concurrency protection.
4. Add PostgreSQL integration coverage and OpenAPI alongside feature work.
5. Verify the full demo, container startup, and release checklist before a `dev` to `main` PR.
6. After Dispatch is release-ready, continue with EAV Ledger.

A PR awaiting review does not block independent documentation, CI, integration coverage, or resource work. Keep dependent branches explicit and leave merge decisions to the maintainer.

## Post-MVP

- Authentication and role-based authorization
- Multi-tenant organization boundaries
- Route planning and optimization
- Asynchronous domain events and notifications
- Operations dashboard
