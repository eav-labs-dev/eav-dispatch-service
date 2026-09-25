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

## Create a driver, vehicle, and shipments

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

waiting_shipment=$(curl --fail-with-body --silent --show-error \
  -X POST "$dispatch_api/shipments" \
  -H 'Content-Type: application/json' \
  --data "{\"reference\":\"WAIT-$demo_suffix\",\"description\":\"Replacement equipment\",\"origin\":\"Accra\",\"destination\":\"Tamale\"}")
waiting_shipment_id=$(printf '%s' "$waiting_shipment" | python3 -c 'import json,sys; print(json.load(sys.stdin)["data"]["id"])')
```

Each create request returns HTTP 201. The driver and vehicle start active, while both shipments start in `CREATED`.

## Assign the first shipment

```bash
curl --fail-with-body -X PUT "$dispatch_api/shipments/$shipment_id/assignment" \
  -H 'Content-Type: application/json' \
  --data "{\"driverId\":\"$driver_id\",\"vehicleId\":\"$vehicle_id\"}"
```

Assignment moves the first shipment to `ASSIGNED` and reserves both resources while it remains active.

## Prove the resource-reservation guard

This request intentionally omits `--fail-with-body` because it must fail.

```bash
curl --include -X PUT "$dispatch_api/shipments/$waiting_shipment_id/assignment" \
  -H 'Content-Type: application/json' \
  --data "{\"driverId\":\"$driver_id\",\"vehicleId\":\"$vehicle_id\"}"
```

Expect HTTP 409 with code `RESOURCE_CONFLICT`. A driver or vehicle already attached to an `ASSIGNED` or `IN_TRANSIT` shipment cannot be double-booked.

## Deliver and release the resources

```bash
curl --fail-with-body -X POST "$dispatch_api/shipments/$shipment_id/transitions" \
  -H 'Content-Type: application/json' \
  --data '{"targetStatus":"IN_TRANSIT","note":"Departed Tema"}'

curl --fail-with-body -X POST "$dispatch_api/shipments/$shipment_id/transitions" \
  -H 'Content-Type: application/json' \
  --data '{"targetStatus":"DELIVERED","note":"Received in Kumasi"}'

curl --fail-with-body -X PUT "$dispatch_api/shipments/$waiting_shipment_id/assignment" \
  -H 'Content-Type: application/json' \
  --data "{\"driverId\":\"$driver_id\",\"vehicleId\":\"$vehicle_id\"}"
```

The controlled transitions move the first shipment through `IN_TRANSIT` to `DELIVERED`. Delivery releases the driver and vehicle, so the waiting shipment can then enter `ASSIGNED`.

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

curl --include -X DELETE "$dispatch_api/shipments/$shipment_id"

curl --fail-with-body "$dispatch_api/shipments/$shipment_id/history"
```

Both mutation requests must return HTTP 409 with code `RESOURCE_CONFLICT`. Delivered shipments cannot transition, be edited, or be hard-deleted. The final history request must still return the four audit entries, proving that operational records remain available after a rejected deletion.

## Finish

```bash
docker compose down --volumes
```

The MVP does not claim public production deployment. Authentication, role-based authorization, pagination, and route optimization remain post-MVP work.
