# E-Commerce Micro

A Jakarta EE web application running on **Payara Micro**, designed as a clustered two-node setup with Hazelcast session replication, an nginx load balancer, and full observability via OpenTelemetry, Prometheus, Grafana, and Loki. It shares the `ecommerce-shared` Docker network with the [testenv](../testenv/README.md) stack, which supplies Keycloak, Prometheus, Grafana, Loki, and Jaeger.

## Prerequisites

- Java 21
- Maven
- Docker & Docker Compose
- The `ecommerce-shared` Docker network (created by `testenv`)

## Services (Docker)

| Service    | Port | Description                          |
|------------|------|--------------------------------------|
| nginx      | 8088 | Load balancer (entry point)          |
| Payara     | 8080 | Application node 1                   |
| Debug 1    | 5005 | Remote debug port — node 1           |
| Payara 2   | 8081 | Application node 2                   |
| Debug 2    | 5006 | Remote debug port — node 2           |
| PostgreSQL | 5432 | Database                             |
| pgAdmin    | 5050 | Database management UI               |

Both nodes cluster via Payara Micro DNS cluster mode (`payara:6900, payara2:6900`).

## Development (local Payara Micro)

Start the database and supporting services (requires testenv running):

```bash
./dev.sh
```

Build and start Payara Micro locally:

```bash
./payara-start.sh
```

Stop Payara Micro:

```bash
./payara-stop.sh
```

Run with remote debug enabled (port 5005):

```bash
./debug.sh
```

## Run in Docker

Build and start all services:

```bash
mvn clean package
docker compose --profile app up
```

The application will be available at <http://localhost:8088> via the nginx load balancer.

## Rebuild & Restart

Full rebuild and restart of all Docker services:

```bash
./rebuild.sh
```

Restart without rebuilding:

```bash
./restart.sh
```

Stop all services:

```bash
./stop.sh
```

## Deploy to Kubernetes (MicroK8s)

Build and push the image to the local MicroK8s registry, then deploy:

```bash
./build-deploy.sh
```

Remove the deployment:

```bash
./undeploy.sh
```

The Kubernetes deployment runs 2 replicas clustered via DNS (`payara-headless:6900`). See `k8s/deployment.yaml` and `k8s/postgres-statefulset.yaml` for details.

## Linting and Code Formatting

```bash
mvn spotless:check
mvn spotless:apply
```
