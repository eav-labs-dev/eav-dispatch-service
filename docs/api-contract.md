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

## Interactive documentation

When the service is running, the generated OpenAPI description is available at `/v3/api-docs` and `/v3/api-docs.yaml`. Swagger UI is available at `/swagger-ui.html`.

The document version uses `APP_VERSION`, which keeps local, release, and deployed API metadata aligned.
## Driver endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/v1/drivers` | Create an active driver |
| `GET` | `/api/v1/drivers` | List drivers |
| `GET` | `/api/v1/drivers/{id}` | Retrieve one driver |
| `PUT` | `/api/v1/drivers/{id}` | Update mutable details and availability |
| `DELETE` | `/api/v1/drivers/{id}` | Delete a driver |

Employee numbers and licence numbers are immutable, trimmed, uppercased, and unique. New drivers are active by default.
