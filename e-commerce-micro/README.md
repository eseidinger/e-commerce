# E-Commerce Micro

## Run Native

```bash
docker compose --profile dev up
mvn clean install
mvn payara-micro:start@start
mvn payara-micro:stop@stop
```

or

```bash
mvn clean install
mvn exec:exec@debug
```

## Run in Docker

```bash
mvn clean package
docker compose --profile app up
```

## Linting and Code Formatting

```bash
mvn spotless:check
mvn spotless:apply
```
