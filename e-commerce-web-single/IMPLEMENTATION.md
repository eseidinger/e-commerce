# E-Commerce Web Single — Implementation

## Architecture Overview

This is a Jakarta EE 11 web application running on a single Payara Server instance. It uses JSF 4.0 for the UI, JPA for persistence, Flyway for database migrations, Quartz for scheduled tasks, and Keycloak for authentication via OpenID Connect.

```
Browser ──► Payara (single instance)
                 ├── JSF (UI)
                 ├── Jakarta Security (OIDC)
                 ├── JPA / PostgreSQL
                 └── Quartz Scheduler
```

## Package Structure

| Package | Responsibility |
|---------|---------------|
| `bean` | JSF backing beans; UI state, access control checks |
| `config` | Startup tasks: Flyway migration, Quartz scheduler |
| `model` | JPA entities |
| `repository` | EntityManager CRUD operations |
| `service` | Business logic, input validation, transactional boundaries |
| `servlet` | HTTP endpoints for OAuth2 callback, logout, session info |
| `exception` | `SecurityException`, `ValidationException` |
| `error/jsf` | Custom JSF exception handler and factory |
| `util` | Static input validation helpers |

## Authentication

Authentication is delegated to Keycloak via the **Jakarta Security OpenID Connect** mechanism.

`SecurityBean` declares the `@OpenIdAuthenticationDefinition` annotation, which configures Payara to handle the full OAuth2 authorization code flow automatically. `OpenIdConfigBean` reads the provider URL, client ID, and redirect URI from environment variables at startup.

**Flow:**
1. User requests a protected JSF page.
2. Payara redirects to Keycloak's authorization endpoint.
3. User authenticates at Keycloak.
4. Keycloak redirects to `/callback` (`CallbackServlet`), which stores session metadata and forwards to the original page.
5. The authenticated principal and roles (`guest`, `admin`) are available via `HttpServletRequest`.

**Logout** is handled by `LogoutServlet`, which invalidates the local HTTP session and redirects to Keycloak's logout endpoint with an `id_token_hint` so the IdP session is also terminated.

**Authorization** uses role checks in JSF backing beans (`CustomerBean` checks `isUserInRole("admin")` before any mutating operation and throws `SecurityException` otherwise). Protected pages require `guest` or `admin` role as declared in `web.xml`.

## Session Management

Sessions are stored **in the JVM heap only**. This is explicitly configured in `glassfish-web.xml`:

```xml
<session-manager persistence-type="memory"/>
```

Session timeout is 30 minutes (`web.xml`). `InstanceInfoBean` tracks a per-session request counter stored as a session attribute. `SessionInfoServlet` at `/session-info` exposes session metadata (hostname, IP, session ID, request count) as JSON for debugging.

**Consequence for scaling:** Because sessions are not persisted or replicated, this application cannot be safely load-balanced across multiple instances. Any second instance would not have access to sessions created on the first. See [e-commerce-web-multi](../e-commerce-web-multi/IMPLEMENTATION.md) for the clustered alternative.

## Data Layer

**ORM:** JPA with the persistence unit `ecommercePU`, bound to the JNDI datasource `jdbc/ecommerceDS` configured in Payara via `preboot-commands.asadmin`. Transactions are container-managed (JTA).

**Entities:**
- `Customer` — `customerId` (PK, serial), `name`, `email` (unique), `address`

**Migrations:** Flyway runs at application startup via `FlywayMigrationBean` (`@Singleton @Startup`):
- `V1__init.sql` — creates the `customer` table
- `V2__quartz_tables_postgres.sql` — creates Quartz scheduler tables

**Access pattern:**
- `CustomerRepository` (`@ApplicationScoped`) — direct EntityManager operations
- `CustomerService` (`@ApplicationScoped`, `@Transactional`) — validates input, delegates to repository
- `CustomerBean` (`@ViewScoped`) — JSF backing bean; calls service from action methods

## Scheduled Tasks

`QuartzLoggerBean` (`@Singleton @Startup`, `@DependsOn("FlywayMigrationBean")`) initializes a Quartz scheduler backed by the PostgreSQL database. It schedules `HostnameLoggerJob` to run every minute, which logs the current container hostname.

**Important limitation:** Quartz is **not** configured for clustering here. If multiple instances were run, each would independently execute the job, causing duplicate runs. For clustered Quartz operation see the multi-instance variant.

## JSF Configuration

- Version 4.0 (`faces-config.xml`)
- State saving method: **server** (default) — view state held in the HTTP session
- Custom `ExceptionHandler` catches unhandled JSF exceptions, logs them, and adds a `FacesMessage` for display

## Why This is a Single-Instance Application

| Aspect | Detail |
|--------|--------|
| Session storage | In-memory, not replicated |
| Hazelcast | On classpath as `provided` scope — not activated |
| `web.xml` | No `<distributable/>` element |
| Quartz | Not clustered; each instance would run jobs independently |
| Load balancer | None — direct access to Payara on port 8080 |
