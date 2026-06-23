# From Single-Node Payara to a Clustered Web Tier

Moving a Jakarta EE application from one app server to multiple nodes is where architecture decisions become operational reality. In this post, I walk through the migration path used in this repository: from `e-commerce-web-single` to `e-commerce-web-multi`.

The key point is simple: scaling web nodes is easy, scaling state is not.

## Why move from single to multi?

A single Payara Server instance is great for local development and straightforward deployments. But once you need better availability, rolling updates, or more throughput, you need multiple instances behind a load balancer.

That changes the rules for:

- HTTP sessions
- Authentication state
- JSF state saving
- Background jobs

In this repository, the multi-node setup uses two Payara instances (`payara`, `payara2`) behind nginx on port `8088`.

## Baseline: what works in single-node mode

The single deployment (`e-commerce-web-single`) is a standard Jakarta EE stack:

- JSF UI
- JPA + PostgreSQL
- Flyway migrations at startup
- Quartz scheduler
- Keycloak login through Jakarta Security OpenID Connect

It stores session state in local JVM memory and uses server-side JSF state saving. That is fine when every request always reaches the same instance.

## The migration checklist

Here is the practical checklist used to move to `e-commerce-web-multi`.

### 1. Add a real entry point with nginx

The multi variant adds nginx as a reverse proxy and load balancer. Traffic comes in on `localhost:8088` and is round-robined across both Payara nodes.

This lets you scale and restart nodes independently without changing client URLs.

### 2. Mark the app as distributable

In `web.xml`, the multi-node app adds:

```xml
<distributable/>
```

This signals that the application is cluster-ready and allows Payara to back session handling with Hazelcast rather than local-only memory.

### 3. Configure Hazelcast discovery for containers

Multicast discovery is fragile in container environments. The multi deployment uses DNS-based discovery via Payara postboot commands and environment variables.

Result: each node can find peers as container hostnames and replicate HTTP session data.

### 4. Make session objects serializable

Once session data crosses node boundaries, every object stored in session must be safely serializable. In this project, domain objects that can end up in session are updated accordingly (for example, explicit `serialVersionUID`).

### 5. Rework authentication for non-sticky load balancing

Single-node OIDC with Jakarta Security is convenient, but round-robin introduces callback and state-correlation risks when login starts on one node and returns to another.

The multi variant uses a custom JWT cookie mechanism:

- access and refresh tokens in secure HttpOnly cookies
- token validation on each node
- refresh flow when access token expires

This makes authentication effectively stateless at the node level and removes dependence on sticky sessions.

### 6. Move JSF view state to the client

Clustered deployments work more reliably when JSF view state is client-side rather than stored in server session memory.

The multi variant sets:

```xml
<context-param>
	<param-name>jakarta.faces.STATE_SAVING_METHOD</param-name>
	<param-value>client</param-value>
</context-param>
```

This avoids view-state loss when requests land on different nodes.

### 7. Review scheduler behavior

Quartz jobs in this setup are intentionally not clustered, so each node runs the same scheduled job. That is useful for node-level heartbeat visibility in logs, but it may be wrong for business-critical one-time jobs.

Before production rollout, decide whether each job should be:

- per-node (duplicate execution expected), or
- singleton across cluster (Quartz clustering required)

## Running the multi-node variant locally

From the repository root:

```bash
cd e-commerce-web-multi
mvn clean package
docker compose build --no-cache
docker compose up
```

Then open:

- Application: `http://localhost:8088`
- Node 1 admin: `http://localhost:4848`
- Node 2 admin: `http://localhost:4849`

To quickly validate replication behavior, call the session info endpoint multiple times and confirm the request counter continues while hostname changes.

## Lessons learned

- Horizontal scaling starts with state strategy, not with more containers.
- Session replication is necessary but not sufficient; auth flow design matters just as much.
- JSF can run well in clusters if state saving is configured intentionally.
- "It works on one node" is not evidence for multi-node correctness.

## Final takeaway

The migration from `e-commerce-web-single` to `e-commerce-web-multi` is not a rewrite. It is a focused set of platform-aware changes: distributable config, clustered session handling, stateless auth, and a fronting load balancer.

That is the pattern I recommend for teams modernizing existing Jakarta EE applications without throwing away the current codebase.
