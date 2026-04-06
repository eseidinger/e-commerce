# E-Commerce Web Single

A Jakarta EE web application deployed on a single Payara Server instance. It uses a PostgreSQL database and is designed as a standalone server setup without clustering.

## Prerequisites

- Java 25
- Maven
- Docker & Docker Compose

## Services

| Service   | Port | Description                        |
|-----------|------|------------------------------------|
| Payara    | 8080 | Application server                 |
| Admin     | 4848 | Payara Admin Console               |
| Debug     | 9009 | Remote debug port                  |
| PostgreSQL| 5432 | Database                           |
| pgAdmin   | 5050 | Database management UI             |

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

The application will be available at <http://localhost:8080>.

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

