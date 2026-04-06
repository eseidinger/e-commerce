# E-Commerce Web Multi — Implementation

## Architecture Overview

This is a Jakarta EE 11 web application designed for **horizontal scaling** across multiple Payara Server instances. Sessions are replicated via Hazelcast, an nginx reverse proxy load-balances traffic, and a custom JWT-based authentication mechanism ensures stateless token verification on every node.

```
Browser ──► nginx (load balancer, :8088)
               ├──► Payara node 1 (:8080)  ──┐
               └──► Payara node 2 (:8081)  ──┤── Hazelcast cluster (port 4900)
                                              │
                                    PostgreSQL (shared, :5432)
                                    Keycloak  (external)
```

## Package Structure

| Package | Responsibility |
|---------|---------------|
| `auth` | Custom JWT cookie authentication mechanism, OIDC servlet flow |
| `bean` | JSF backing beans; UI state, access control checks |
| `config` | Startup tasks: Flyway migration, Quartz scheduler |
| `model` | JPA entities |
| `repository` | EntityManager CRUD operations |
| `service` | Business logic, input validation, transactional boundaries |
| `servlet` | HTTP endpoints: login, callback, logout, logout-callback, session info |
| `exception` | `SecurityException`, `ValidationException` |
| `error/jsf` | Custom JSF exception handler and factory |
| `util` | Static input validation helpers |

## Running Multiple Instances

### Distributable Application (`web.xml`)

The single most important difference from the single-instance variant is the presence of `<distributable/>` in `web.xml`:

```xml
<distributable/>
```

This flag tells Payara that the application supports clustering. Payara then delegates session storage to Hazelcast instead of local JVM memory.

### Hazelcast Session Replication

Hazelcast is activated and configured via `postboot-commands.asadmin`, which runs after Payara starts:

```asadmin
set-hazelcast-configuration --clustermode dns --dnsmembers ${ENV=HAZELCAST_MEMBERS} --dynamic=true
```

- **Cluster mode:** DNS — nodes discover each other by hostname rather than multicast (required for Docker/container environments)
- **Members:** resolved from the `HAZELCAST_MEMBERS` environment variable (`payara:4900,payara2:4900` in `docker-compose.yml`)
- **Port:** 4900 (default Payara Hazelcast port)
- **Dynamic:** configuration changes are applied at runtime without a restart

When a session attribute is written on node 1, Hazelcast replicates it to node 2. Any subsequent request routed to node 2 by nginx finds the session intact. This makes **sticky sessions unnecessary**, though they could optionally be added at the nginx layer.

To confirm replication is working, `SessionInfoServlet` at `/session-info` returns a JSON payload containing the hostname, IP, session ID, and a per-session request counter. Repeat requests routed to different nodes will show different hostnames but a continuously incrementing counter — proving the session travelled between nodes.

### nginx Load Balancer

`nginx/nginx.conf` defines both Payara nodes as an upstream group:

```nginx
upstream payara_backend {
    server payara:8080;
    server payara2:8080;
}
```

nginx uses **round-robin** by default. All proxy headers (`X-Real-IP`, `X-Forwarded-For`, `X-Forwarded-Proto`, `Host`) are forwarded so that the application can reconstruct the original client request. Traffic enters on port 80 inside the container, exposed as port 8088 on the host.

### Session-Safe Entity Serialization

All session-stored objects must be serializable across nodes. `Customer` implements `java.io.Serializable` with an explicit `serialVersionUID`, ensuring consistent deserialization when session data is transferred between nodes by Hazelcast.

## Authentication

Unlike the single-instance application (which uses Jakarta Security's `@OpenIdAuthenticationDefinition`), this application implements a **custom JWT-based authentication mechanism** to remain stateless and cluster-safe.

### Why Custom JWT Instead of Jakarta Security OIDC?

Jakarta Security's built-in OIDC support stores authentication state in the HTTP session, which can cause replay and state-mismatch issues under round-robin load balancing during the authorization code flow (the callback may arrive at a different node than the one that initiated the login). Using JWT cookies avoids this: token validation is performed independently on whichever node receives the request.

### Custom Authentication Mechanism (`auth` package)

`CustomJwtAuthentication` (`@ApplicationScoped`) implements `HttpAuthenticationMechanism` and delegates to `JwtCookieHandler` for `/jsf/*` paths.

**`JwtCookieHandler`** — reads the JWT from a cookie, validates it, and silently refreshes it using the refresh token cookie if it has expired. If the refresh also fails, the user is redirected to `/login`.

**`JwtUtils`** — performs cryptographic JWT verification:
1. Fetches the RSA public key from Keycloak's JWKS endpoint.
2. Verifies the signature and expiration.
3. Returns a `TokenInfo` record containing username, roles, and expiration.

### OAuth2 Authorization Code Flow

The flow is implemented via four servlets:

| Servlet | Endpoint | Role |
|---|---|---|
| `LoginServlet` | `/login` | Redirects browser to Keycloak authorization endpoint |
| `CallbackServlet` | `/callback` | Exchanges authorization code for JWT tokens; stores tokens in secure HttpOnly cookies |
| `LogoutServlet` | `/logout` | Redirects to Keycloak logout endpoint |
| `LogoutCallbackServlet` | `/logout-callback` | Clears JWT cookies; redirects to home |

Token storage in HttpOnly cookies means the tokens are available on every node without depending on session replication, keeping authentication fully stateless.

### JSF State Saving: Client-Side

`web.xml` configures JSF to save view state on the **client** (in a hidden form field):

```xml
<param-name>jakarta.faces.STATE_SAVING_METHOD</param-name>
<param-value>client</param-value>
```

This keeps server-side memory usage low and ensures view state is not lost when a request is routed to a different node.

## Data Layer

Identical in structure to the single-instance variant. Both Payara nodes share a single PostgreSQL database. Each obtains its own JDBC connection pool (`ecommercePool` → `jdbc/ecommerceDS`) configured via `preboot-commands.asadmin`.

**Entities:** `Customer` (serializable, as noted above)

**Migrations:** Flyway runs at startup on whichever node initializes first. Both nodes read `FLYWAY_LOCATIONS` from the classpath and apply pending migrations. In practice, one node wins the migration lock and the other waits.

## Scheduled Tasks

`QuartzLoggerBean` initializes a Quartz scheduler backed by the PostgreSQL job store. The `HostnameLoggerJob` fires every minute and logs the container hostname, which is useful for confirming which node executed the job.

Unlike a production clustered Quartz setup (where `org.quartz.jobStore.isClustered=true` would prevent duplicate executions), here each node runs the job independently. This is intentional for observability — each node's log shows its own heartbeat.

## Comparison: Single vs. Multi-Instance

| Aspect | e-commerce-web-single | e-commerce-web-multi |
|--------|----------------------|---------------------|
| `<distributable/>` | absent | present |
| Session storage | JVM heap only | Hazelcast (replicated) |
| Hazelcast | not activated | DNS cluster, port 4900 |
| Authentication | Jakarta Security OIDC (`@OpenIdAuthenticationDefinition`) | Custom JWT cookies + manual OIDC flow |
| JSF state saving | server (session) | client (hidden field) |
| Load balancer | none | nginx round-robin |
| Nodes | 1 | 2 (`payara`, `payara2`) |
| Entry point | port 8080 (directly) | port 8088 (via nginx) |
| `Customer` serializable | not required | required (`serialVersionUID`) |
