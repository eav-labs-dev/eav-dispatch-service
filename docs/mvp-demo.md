# Dispatch MVP reviewer demo

This walkthrough proves the core logistics workflow from resource setup to immutable delivery history. Run it against a disposable local environment because it creates records.

## Start and verify the stack

Requirements: Docker Engine with Docker Compose v2, curl, Bash or Zsh, and Python 3.

```bash
docker compose up --build --detach
until curl --fail --silent http://localhost:8080/actuator/health; do sleep 2; done
```

The API base URL is:

```bash
dispatch_api=http://localhost:8080/api/v1
demo_suffix=$(python3 -c 'import uuid; print(str(uuid.uuid4())[:8].upper())')
licence_expiry=$(python3 -c 'from datetime import date; print(date(date.today().year + 2, 12, 31))')
```

## Create a driver, vehicle, and shipment

```bash
driver=$(curl --fail-with-body --silent --show-error \
  -X POST "$dispatch_api/drivers" \
  -H 'Content-Type: application/json' \
  --data "{\"employeeNumber\":\"DRV-$demo_suffix\",\"fullName\":\"Ama Mensah\",\"phoneNumber\":\"+233 24 000 0001\",\"licenseNumber\":\"LIC-$demo_suffix\",\"licenseClass\":\"C\",\"licenseExpiresOn\":\"$licence_expiry\"}")
driver_id=$(printf '%s' "$driver" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["id"])')

vehicle=$(curl --fail-with-body --silent --show-error \
  -X POST "$dispatch_api/vehicles" \
  -H 'Content-Type: application/json' \
  --data "{\"registrationNumber\":\"GT $demo_suffix\",\"make\":\"Volvo\",\"model\":\"FH16\",\"type\":\"TRUCK\",\"maxPayloadKg\":25000}")
vehicle_id=$(printf '%s' "$vehicle" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["id"])')

shipment=$(curl --fail-with-body --silent --show-error \
  -X POST "$dispatch_api/shipments" \
  -H 'Content-Type: application/json' \
  --data "{\"reference\":\"DSP-$demo_suffix\",\"description\":\"Medical equipment\",\"origin\":\"Tema\",\"destination\":\"Kumasi\"}")
shipment_id=$(printf '%s' "$shipment" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["id"])')
```

Each create request returns HTTP 201. The driver and vehicle start active, while the shipment starts in `CREATED`.

## Assign and deliver the shipment

```bash
curl --fail-with-body -X PUT "$dispatch_api/shipments/$shipment_id/assignment" \
  -H 'Content-Type: application/json' \
  --data "{\"driverId\":\"$driver_id\",\"vehicleId\":\"$vehicle_id\"}"

curl --fail-with-body -X POST "$dispatch_api/shipments/$shipment_id/transitions" \
  -H 'Content-Type: application/json' \
  --data '{"targetStatus":"IN_TRANSIT","note":"Departed Tema"}'

curl --fail-with-body -X POST "$dispatch_api/shipments/$shipment_id/transitions" \
  -H 'Content-Type: application/json' \
  --data '{"targetStatus":"DELIVERED","note":"Received in Kumasi"}'
```

Assignment moves the shipment to `ASSIGNED`. The controlled transitions then move it through `IN_TRANSIT` to the terminal `DELIVERED` state.

## Inspect the audit history

```bash
curl --fail-with-body "$dispatch_api/shipments/$shipment_id"
curl --fail-with-body "$dispatch_api/shipments/$shipment_id/history"
```

The history response must list `CREATED`, `ASSIGNED`, `IN_TRANSIT`, and `DELIVERED` in chronological order. The final shipment must retain the assigned driver and vehicle identifiers.

## Prove the lifecycle guard

This request is expected to fail, so it intentionally omits `--fail-with-body`.

```bash
curl --include -X POST "$dispatch_api/shipments/$shipment_id/transitions" \
  -H 'Content-Type: application/json' \
  --data '{"targetStatus":"IN_TRANSIT","note":"Invalid reversal"}'
```

Expect HTTP 409 with code `RESOURCE_CONFLICT`. Delivered shipments cannot transition or be edited.

## Finish

```bash
docker compose down --volumes
```

The MVP does not claim public production deployment. Authentication, role-based authorization, pagination, and route optimization remain post-MVP work.
