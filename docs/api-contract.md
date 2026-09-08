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
| `DELETE` | `/api/v1/shipments/{id}` | Delete a shipment |

Lifecycle transitions are intentionally excluded from CRUD requests and will be exposed through a dedicated transition endpoint.
