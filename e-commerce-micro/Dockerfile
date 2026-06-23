# Payara Micro Dockerfile
FROM maven:3.9.14-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml ./
COPY src ./src
RUN mvn -B clean package

FROM payara/micro:7.2026.3-jdk25

# Copy the built WAR to the Payara deployments folder.
COPY --from=build /build/target/e-commerce-micro.war /opt/payara/deployments/e-commerce-micro.war

# Copy logging configuration
COPY ./config/logging.properties /opt/payara/logging.properties

# Optionally set JVM options for debugging, memory, etc.
ENV JVM_ARGS="-Djdk.util.zip.disableZip64ExtraFieldValidation=true"

# Expose HTTP and debug ports
EXPOSE 8080 5005

# Entrypoint is inherited from payara/micro
