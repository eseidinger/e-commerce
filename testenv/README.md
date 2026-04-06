# Test Environment

Provides shared infrastructure services for local development and testing. All services join the `ecommerce-shared` Docker network so that application stacks (e.g. `e-commerce-micro`) can reach them by hostname.

## Services

| Service    | Port(s)       | Description                                  |
|------------|---------------|----------------------------------------------|
| Keycloak   | 8084          | Identity provider (OIDC/OAuth2); imports realm from `keycloak/` on startup |
| Prometheus | 9090          | Metrics collection; scrapes `host.docker.internal:8080`, `payara:8080`, `payara2:8080` |
| Grafana    | 3001          | Dashboards for metrics (Prometheus) and logs (Loki) |
| Loki       | 3100          | Log aggregation backend                      |
| Promtail   | 9080          | Log shipper; reads container logs via Docker socket and forwards to Loki |
| Jaeger     | 16686 (UI), 4317 (OTLP/gRPC), 4318 (OTLP/HTTP) | Distributed tracing backend |

## Prerequisites

- Docker & Docker Compose
- The `ecommerce-shared` network must exist before starting any application stack:

```bash
docker network create ecommerce-shared
```

## Start

```bash
docker compose up -d
```

## Keycloak

The realm configuration is imported automatically at startup from `keycloak/e-commerce-dev-realm.json`. Users are imported from `keycloak/e-commerce-dev-users-0.json`.

- Admin UI: <http://localhost:8084>
- Admin credentials: `admin` / `admin`
- Realm: `e-commerce-dev`

## Observability

### Prometheus

Scrapes metrics from:
- `host.docker.internal:8080` — application running natively on the host
- `payara:8080`, `payara2:8080` — Payara instances running inside Docker

Configuration: `prometheus.yml`

### Grafana

- UI: <http://localhost:3001>
- Pre-provisioned datasources: Prometheus and Loki
- Pre-provisioned dashboards: Payara Micro metrics, Docker container logs (via Loki)

### Loki & Promtail

Promtail discovers all running containers via the Docker socket and ships their logs to Loki. Logs are labelled by container name, compose service, compose project, and log stream. Configuration: `promtail.yml`

### Jaeger

Receives traces from application instances via OTLP (gRPC port 4317, HTTP port 4318).

- UI: <http://localhost:16686>

Applications must set the environment variable:

```
OTEL_EXPORTER_OTLP_ENDPOINT=http://jaeger:4317
```
