FROM alpine/curl:8.14.1 AS build

# Download PostgresSQL driver
RUN curl -o /postgresql.jar https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.7/postgresql-42.7.7.jar

FROM payara/server-web:5.2022.5-jdk17

# Copy preboot and postboot scripts
COPY scripts/preboot-commands.asadmin $PREBOOT_COMMANDS
COPY scripts/postboot-commands.asadmin $POSTBOOT_COMMANDS

# Copy WAR file
COPY deploy/e-commerce-payara5.war $HOME_DIR/e-commerce-payara5.war

# Copy PostgreSQL JDBC driver to Payara lib directory
COPY --from=build /postgresql.jar $PAYARA_DIR/glassfish/lib/
