# E-Commerce Micro — Implementation

## Architecture Overview

`e-commerce-micro` is a Jakarta EE 11 application on Payara Micro with both JSF pages and REST APIs.

At runtime (Docker app profile), traffic flows through nginx to two Payara Micro nodes, both connected to one PostgreSQL database and external Keycloak for identity.

```text
Browser -> nginx (:8088)
            -> payara (:8080)
            -> payara2 (:8081)

payara/payara2 -> PostgreSQL (:5432)
payara/payara2 -> Keycloak (OIDC)
payara/payara2 -> Jaeger OTLP (:4317) for tracing
```

## Package Responsibilities

| Package | Responsibility |
|---|---|
| `auth` | Authentication mechanism and OIDC/JWT handling |
| `auth/jsf` | Login/callback/logout servlets and cookie-based JWT handling |
| `auth/jwt` | Bearer-token handling for API requests |
| `auth/utils` | JWT decode/verify helper utilities |
| `rest` | JAX-RS resources under `/api` |
| `bean` | JSF backing beans for server-rendered pages |
| `service` | Business logic and transaction boundaries |
| `repository` | JPA persistence operations via `EntityManager` |
| `model` | JPA entities |
| `dto` | DTO records used by REST layer |
| `config` | Startup/bootstrap concerns (Flyway, Quartz, JAX-RS app config) |
| `error` | REST exception mappers and JSF exception handling |
| `listener` | SQL trace listener integration point |
| `util` | Input validation helpers |

## Authentication and Authorization

Authentication is custom and path-based in `CustomJwtAuthentication`:

- Requests beginning with `/api` are validated via `JwtHeaderHandler` (Bearer token in `Authorization` header).
- Requests beginning with `/jsf` are validated via `JwtCookieHandler` (JWT stored in HttpOnly cookies).

OIDC flow is servlet-driven:

1. `LoginServlet` redirects to Keycloak authorize endpoint.
2. `CallbackServlet` exchanges auth code for tokens.
3. `id_token` and refresh token are stored in cookies.
4. `JwtCookieHandler` validates or refreshes tokens on subsequent JSF requests.
5. `LogoutServlet` and `LogoutCallbackServlet` clear local/session auth state and complete IdP logout.

Role model:

- Roles are declared as `guest` and `admin`.
- REST reads typically allow `guest`/`admin`.
- REST writes are restricted to `admin`.
- JSF mutation operations enforce admin checks in backing beans/services.

## Session and Cluster Behavior

`WEB-INF/web.xml` includes:

- `<distributable/>` to mark the app as cluster-capable.
- Session timeout of 30 minutes.
- JSF state saving configured as `client`.

Cluster topology:

- Docker app profile runs two Payara nodes (`payara`, `payara2`).
- Payara Micro cluster discovery uses DNS-based cluster mode:
  - Docker: `dns:payara:6900,payara2:6900`
  - Kubernetes: `dns:payara-headless:6900`

`SessionInfoServlet` exposes runtime instance/session metadata (hostname, IP, session ID, counters) and is useful to validate behavior while load-balanced.

## Persistence and Transactions

Datasource and JPA:

- JTA datasource is defined directly in `web.xml` as `java:global/ecommerceDS`.
- PostgreSQL is used as backing database.
- `persistence.xml` binds entities to persistence unit `ecommercePU`.

Data access pattern:

1. REST/JSF layers call services.
2. Services apply validation and business rules.
3. Repositories use `EntityManager` for CRUD.
4. Transaction boundaries are on service methods with `@Transactional`.

Flyway:

- `FlywayMigrationBean` runs at startup.
- SQL migrations under `src/main/resources/db/migration` create domain tables and Quartz tables.

## Quartz Scheduling in a Cluster

`quartz.properties` configures JDBC job store with clustering enabled:

- `org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX`
- `org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.PostgreSQLDelegate`
- `org.quartz.jobStore.isClustered=true`
- JNDI datasource: `java:global/ecommerceDS`

`QuartzLoggerBean` starts scheduler after Flyway and schedules hostname logging jobs. With clustered JDBC job store, Quartz coordinates via DB locks to avoid duplicate execution across nodes.

## Load Balancing

`nginx/nginx.conf` defines an upstream with both nodes:

- `payara:8080`
- `payara2:8080`

Requests are proxied in round-robin mode by default and forwarded with standard `X-Forwarded-*` headers.

## Observability

The app is prepared for shared test environment observability:

- OpenTelemetry export endpoint is injected via `OTEL_EXPORTER_OTLP_ENDPOINT`.
- Docker app profile points to Jaeger OTLP (`http://jaeger:4317`).
- Logging is configured through `config/logging.properties`.
- A SQL trace listener hook (`DummySqlTraceListener`) is wired in the datasource config.

## Deployment Modes

### Local Development

- `./dev.sh` starts DB + pgAdmin profile.
- `./payara-start.sh` starts Payara Micro from Maven plugin.
- `./debug.sh` starts microbundle with remote debugging.

### Docker (clustered)

- `docker compose --profile app up` starts nginx + two Payara nodes + DB + pgAdmin.
- Entry point for browser traffic is `http://localhost:8088`.

### Kubernetes (MicroK8s)

- `k8s/postgres-statefulset.yaml` provisions PostgreSQL.
- `k8s/deployment.yaml` runs 2 Payara replicas.
- Headless service provides DNS for intra-cluster discovery.
- `build-deploy.sh` automates image push and manifest apply.

## Multi-Instance Characteristics (vs single-node)

| Aspect | e-commerce-micro |
|---|---|
| App nodes | 2 nodes in Docker app profile / K8s replicas |
| Session strategy | Marked distributable; cluster mode enabled |
| Load balancing | nginx upstream (round-robin) |
| Scheduler | Quartz JDBC clustered mode |
| Auth handling | JWT for both API headers and JSF cookies |
| Shared persistence | Single PostgreSQL DB for all nodes |
