# E-Commerce Web Multi

A Jakarta EE web application deployed across two clustered Payara Server instances with Hazelcast session replication and an nginx load balancer in front.

## Prerequisites

- Java 25
- Maven
- Docker & Docker Compose

## Services

| Service    | Port | Description                                  |
|------------|------|----------------------------------------------|
| nginx      | 8088 | Load balancer (entry point)                  |
| Payara     | 8080 | Application server — node 1                  |
| Admin      | 4848 | Payara Admin Console — node 1                |
| Debug      | 9009 | Remote debug port — node 1                   |
| Payara 2   | 8081 | Application server — node 2                  |
| Admin 2    | 4849 | Payara Admin Console — node 2                |
| Debug 2    | 9010 | Remote debug port — node 2                   |
| PostgreSQL | 5432 | Database                                     |
| pgAdmin    | 5050 | Database management UI                       |

## Development

Start the database and supporting services:

```bash
./dev.sh
```

Then run the application locally with Maven.

## Run in Docker

Build and start all services:

```bash
mvn clean package
docker compose build --no-cache
docker compose up
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

