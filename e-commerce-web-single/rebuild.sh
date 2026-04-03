docker compose --profile app down
mvn clean install
docker compose --profile app build --no-cache
docker compose --profile app up -d
