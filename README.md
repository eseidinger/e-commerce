# E-Commerce Migration Playground

This repository contains multiple implementations of the same e-commerce domain to compare deployment models and migration paths:

- Traditional Jakarta EE web app (single instance)
- Jakarta EE web app with clustered Payara nodes
- Payara Micro based service with Docker and Kubernetes deployment
- Angular frontend
- Shared local test/observability environment
- Playwright end-to-end test project

## Repository Structure

| Module | Purpose |
|---|---|
| [e-commerce-web-single](e-commerce-web-single/README.md) | JSF/Jakarta EE app on one Payara Server instance |
| [e-commerce-web-multi](e-commerce-web-multi/README.md) | JSF/Jakarta EE app on two Payara Server nodes behind nginx |
| [e-commerce-micro](e-commerce-micro/README.md) | Payara Micro based app with REST + JSF, clustering, and K8s manifests |
| [e-commerce-ng](e-commerce-ng/README.md) | Angular frontend |
| [testenv](testenv/README.md) | Shared infra: Keycloak, Prometheus, Grafana, Loki, Promtail, Jaeger |
| [e2e-tests](e2e-tests/README.md) | Playwright tests |
| [keycloak](keycloak/) | Realm and users import JSON files for local identity setup |
| [notes](notes/) | Project notes and documentation |

## Architecture Snapshot

The runtime stacks are designed to share one external Docker network named ecommerce-shared.

- testenv provides identity and observability services
- app modules (especially e-commerce-micro and e-commerce-web-multi) connect to that shared network
- nginx fronts multi-node deployments
- PostgreSQL is the primary data store in each app stack

## Prerequisites

- Java 21 / 25
- Maven
- Docker and Docker Compose
- Node.js (for Angular and Playwright workflows)

## Quick Start (Recommended)

1. Create the shared Docker network (once):

```bash
docker network create ecommerce-shared
```

2. Start shared infrastructure:

```bash
cd testenv
docker compose up -d
```

3. Start one application stack (example: e-commerce-micro):

```bash
cd ../e-commerce-micro
mvn clean package
docker compose --profile app up -d
```

4. Open the main endpoints:

- App (micro): http://localhost:8088
- Keycloak: http://localhost:8084
- Grafana: http://localhost:3001
- Prometheus: http://localhost:9090
- Jaeger: http://localhost:16686

## Working with Each Variant

### Single Instance Web

See [e-commerce-web-single/README.md](e-commerce-web-single/README.md) and implementation details in [e-commerce-web-single/IMPLEMENTATION.md](e-commerce-web-single/IMPLEMENTATION.md).

### Multi Instance Web

See [e-commerce-web-multi/README.md](e-commerce-web-multi/README.md) and implementation details in [e-commerce-web-multi/IMPLEMENTATION.md](e-commerce-web-multi/IMPLEMENTATION.md).

### Micro Profile Variant

See [e-commerce-micro/README.md](e-commerce-micro/README.md) and implementation details in [e-commerce-micro/IMPLEMENTATION.md](e-commerce-micro/IMPLEMENTATION.md).

## Frontend

Angular app lives in [e-commerce-ng](e-commerce-ng/). Typical local run:

```bash
cd e-commerce-ng
npm install
npm start
```

## End-to-End Tests

Playwright tests live in [e2e-tests](e2e-tests/). Typical run:

```bash
cd e2e-tests
npm install
npx playwright test
```

## Shutdown

Stop whichever app stack you started, then stop the shared environment.

Example:

```bash
cd e-commerce-micro
docker compose --profile app down

cd ../testenv
docker compose down
```

## Goal of This Repository

The project is intended to make it easy to compare behavior and operational characteristics across deployment styles, especially:

- single-node vs clustered runtime
- session behavior in multi-instance setups
- auth integration with Keycloak
- observability in local environments
- migration path from classic app server style to micro/deployable runtime
