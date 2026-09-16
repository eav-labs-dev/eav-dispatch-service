# Shipment API Contract

All JSON responses use the envelope:

```json
{
  "success": true,
  "code": "SHIPMENT_RETRIEVED",
  "message": "Shipment retrieved",
  "data": {},
  "error": null
}
```

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/v1/shipments` | Create a shipment in `CREATED` state |
| `GET` | `/api/v1/shipments` | List shipments |
| `GET` | `/api/v1/shipments/{id}` | Retrieve one shipment |
| `PUT` | `/api/v1/shipments/{id}` | Update editable shipment details |
| `PUT` | `/api/v1/shipments/{id}/assignment` | Assign an active driver and vehicle |
| `POST` | `/api/v1/shipments/{id}/transitions` | Apply an allowed lifecycle transition |
| `GET` | `/api/v1/shipments/{id}/history` | Read chronological lifecycle audit history |
| `DELETE` | `/api/v1/shipments/{id}` | Delete a shipment |

Assignments move shipments from `CREATED` to `ASSIGNED`. The transition endpoint permits `ASSIGNED` to `IN_TRANSIT`, `IN_TRANSIT` to `DELIVERED`, and cancellation from `CREATED` or `ASSIGNED`. Terminal states cannot transition again.

## Vehicle endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/v1/vehicles` | Create an active vehicle |
| `GET` | `/api/v1/vehicles` | List vehicles |
| `GET` | `/api/v1/vehicles/{id}` | Retrieve one vehicle |
| `PUT` | `/api/v1/vehicles/{id}` | Update fleet details and availability |
| `DELETE` | `/api/v1/vehicles/{id}` | Delete a vehicle |

## Driver endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/v1/drivers` | Create an active driver |
| `GET` | `/api/v1/drivers` | List drivers |
| `GET` | `/api/v1/drivers/{id}` | Retrieve one driver |
| `PUT` | `/api/v1/drivers/{id}` | Update mutable details and availability |
| `DELETE` | `/api/v1/drivers/{id}` | Delete a driver |

Employee numbers and licence numbers are immutable, trimmed, uppercased, and unique. New drivers are active by default.
