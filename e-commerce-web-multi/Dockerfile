FROM alpine/curl:8.17.0 AS build

# Download PostgresSQL driver
RUN curl -o /postgresql.jar https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.10/postgresql-42.7.10.jar

FROM payara/server-web:7.2026.3-jdk25

# Copy preboot and postboot scripts
COPY scripts/preboot-commands.asadmin $PREBOOT_COMMANDS
COPY scripts/postboot-commands.asadmin $POSTBOOT_COMMANDS

# Copy WAR file
COPY deploy/e-commerce-web.war $HOME_DIR/e-commerce-web.war

# Copy PostgreSQL JDBC driver to Payara lib directory
COPY --from=build /postgresql.jar $PAYARA_DIR/glassfish/lib/
