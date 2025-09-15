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
java -jar target/e-commerce-micro-microbundle.jar --nocluster
```

## Run in Docker

```bash
mvn clean package
docker compose --profile app up
```
