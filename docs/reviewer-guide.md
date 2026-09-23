# Shipment reviewer guide

This walkthrough covers the shipment CRUD implementation on `dev`. It does not exercise assignment or lifecycle transitions, which are not implemented yet.

## Start the service

From the repository root, run:

```bash
docker compose up --build
```

In another terminal, confirm health:

```bash
curl --fail-with-body http://localhost:8080/api/v1/health
```

Use a local disposable development database for the walkthrough. The current API has no authentication or authorization; public deployment requires access controls.

## Create and inspect a shipment

The commands below require Bash or Zsh, curl, and Python 3. Each run uses a new business reference. An omitted pickup time is valid and avoids a demo date becoming stale.

```bash
dispatch_api=http://localhost:8080/api/v1
dispatch_reference="DEMO-$(python3 -c 'import uuid; print(uuid.uuid4())')"
dispatch_created=$(curl --fail-with-body --silent --show-error \
  -X POST "$dispatch_api/shipments" \
  -H 'Content-Type: application/json' \
  --data "{\"reference\":\"$dispatch_reference\",\"description\":\"Equipment delivery\",\"origin\":\"Accra\",\"destination\":\"Kumasi\"}")
printf '%s\n' "$dispatch_created"
dispatch_id=$(printf '%s' "$dispatch_created" | python3 -c 'import json, sys; print(json.load(sys.stdin)["data"]["id"])')
curl --fail-with-body "$dispatch_api/shipments/$dispatch_id"
curl --fail-with-body "$dispatch_api/shipments"
```

Create returns HTTP 201 with code `SHIPMENT_CREATED`, a UUID, and status `CREATED`. References are trimmed and uppercased. Retrieve and list return HTTP 200; list currently returns an unpaginated array.

## Update details

```bash
curl --fail-with-body -X PUT "$dispatch_api/shipments/$dispatch_id" \
  -H 'Content-Type: application/json' \
  --data '{"description":"Updated equipment delivery","origin":"Tema","destination":"Tamale","scheduledPickupAt":null}'
```

Expect HTTP 200 and `SHIPMENT_UPDATED`. The ID, reference, and lifecycle status remain unchanged. PUT supplies all editable details; it is not a partial update.

## Inspect expected failures

These commands intentionally omit `--fail-with-body` so the HTTP status and error body remain visible.

Duplicate reference:

```bash
curl --include -X POST "$dispatch_api/shipments" \
  -H 'Content-Type: application/json' \
  --data "{\"reference\":\"$dispatch_reference\",\"description\":\"Duplicate example\",\"origin\":\"Accra\",\"destination\":\"Kumasi\"}"
```

Expect HTTP 409 and `RESOURCE_CONFLICT` for this sequential duplicate request.

Blank required fields:

```bash
curl --include -X POST "$dispatch_api/shipments" \
  -H 'Content-Type: application/json' \
  --data '{"reference":"","description":"","origin":"","destination":""}'
```

Expect HTTP 400 and `VALIDATION_FAILED`, with details in `error.fields`.

## Remove the demo record

This deletes only the shipment created above:

```bash
curl --fail-with-body -X DELETE "$dispatch_api/shipments/$dispatch_id"
curl --include "$dispatch_api/shipments/$dispatch_id"
```

Delete returns HTTP 200 and `SHIPMENT_DELETED`. The subsequent GET returns HTTP 404 and `RESOURCE_NOT_FOUND`.

Stop the local stack while retaining its database volume:

```bash
docker compose down
```

## Verification limits

Run `./mvnw verify` for the current CI checks. Application tests use H2 with PostgreSQL compatibility mode. A green Maven run alone does not establish PostgreSQL or container startup compatibility.

Before release, run this walkthrough against the Compose PostgreSQL service, add PostgreSQL Testcontainers coverage, and complete the [deployment release gate](deployment.md). There is no claimed live production deployment.
