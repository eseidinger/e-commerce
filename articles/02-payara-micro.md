# Running Payara Micro in the Cloud with Built-In Observability

Payara Micro is often presented as "just run a JAR," but real-world services need more: clustering, external identity, reproducible infrastructure, and telemetry you can actually use.

This article shows how the `e-commerce-micro` module in this repository packages those concerns into a practical cloud-ready setup.

## What this deployment includes

The Payara Micro stack is intentionally opinionated:

- two Payara Micro nodes for horizontal scaling
- nginx as entry point and load balancer
- PostgreSQL as shared persistence
- Hazelcast cluster for session replication
- OpenTelemetry traces/metrics/logs
- Prometheus, Grafana, Loki, and Jaeger in shared `testenv`
- Keycloak-based identity from the same shared environment

The resulting topology is close to what teams run in staging and production, while still being easy to spin up locally.

## Local architecture at a glance

```text
Browser -> nginx:8088
			 |- payara:8080  (debug 5005)
			 |- payara2:8080 (debug 5006)

Both nodes -> PostgreSQL
Both nodes -> Keycloak (testenv)
Both nodes -> OTel pipeline -> Prometheus/Grafana/Loki/Jaeger (testenv)
```

A shared Docker network (`ecommerce-shared`) lets app containers and observability/identity services communicate without ad-hoc host wiring.

## Why Payara Micro here instead of full app server images?

Payara Micro is a strong fit when you want:

- immutable container images
- quick startup for replicas
- standard Jakarta EE programming model
- straightforward clustering in container platforms

You still keep familiar Jakarta APIs, but operationally it behaves like a modern service runtime.

## Build and run flow

Start shared dependencies first:

```bash
cd testenv
docker network create ecommerce-shared   # one-time setup
docker compose up -d
```

Start the micro app stack:

```bash
cd ../e-commerce-micro
mvn clean package
docker compose --profile app up -d
```

Main entry points:

- App: `http://localhost:8088`
- Keycloak: `http://localhost:8084`
- Grafana: `http://localhost:3001`
- Prometheus: `http://localhost:9090`
- Jaeger: `http://localhost:16686`

For iterative local work you can also use the helper scripts:

- `./dev.sh` to start local dependencies
- `./payara-start.sh` / `./payara-stop.sh`
- `./debug.sh` for remote debugging
- `./rebuild.sh` for full refresh

## Session and cluster behavior

The micro deployment runs two nodes and uses Hazelcast DNS member discovery (`payara:6900,payara2:6900`). This is important for both scaling and resilience:

- requests can land on either node
- user session data remains available across nodes
- rolling restarts become safer

When testing, call any session-debug endpoint repeatedly while generating traffic and verify continuity across host switches.

## Observability strategy

The strongest part of this setup is not that telemetry exists, but that it is available by default in dev.

### Metrics

Prometheus scrapes service metrics, allowing fast checks for throughput, error rates, and latency trends.

### Logs

Logs are shipped through Promtail into Loki, then explored in Grafana. Node-specific logs make it easy to distinguish whether behavior is isolated or systemic.

### Traces

Distributed traces in Jaeger make cross-service flows visible, including auth redirects and backend requests that are otherwise hard to reason about from logs alone.

### Why this matters

Without telemetry, cluster bugs are guesswork. With telemetry, you can answer concrete questions:

- Did both nodes receive traffic?
- Did one node show higher latency?
- Was a failed request tied to auth refresh, DB wait, or application logic?

## Kubernetes path (MicroK8s)

The module also includes Kubernetes manifests and scripts for local cluster deployment:

```bash
cd e-commerce-micro
./build-deploy.sh
```

This builds and pushes the image to the local MicroK8s registry, then deploys app + database resources, including:

- deployment with 2 Payara replicas
- headless service for DNS-based cluster discovery
- PostgreSQL stateful set

To tear down:

```bash
./undeploy.sh
```

## Production-minded defaults to keep

If you adapt this setup, keep these defaults:

- externalized environment-driven configuration
- explicit cluster member discovery
- one ingress/load-balancer entry point
- first-class telemetry from day one
- repeatable scripts for build, deploy, and shutdown

These are the small habits that prevent "works locally, fails in cluster" incidents.

## Final takeaway

Running Payara Micro in the cloud is less about the runtime itself and more about operational composition: load balancing, clustering, identity integration, and observability as a baseline.

This repository demonstrates that you can keep Jakarta EE ergonomics while adopting cloud-native deployment and diagnostics practices from the start.
