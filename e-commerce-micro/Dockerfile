# Payara Micro Dockerfile
FROM payara/micro:6.2025.8-jdk21

# Copy application artifacts (WAR or EAR) to deployments folder
COPY ./deploy /opt/payara/deployments

# Copy logging configuration
COPY ./config/logging.properties /opt/payara/logging.properties

# Optionally set JVM options for debugging, memory, etc.
ENV JVM_ARGS="-Djdk.util.zip.disableZip64ExtraFieldValidation=true"

# Expose HTTP and debug ports
EXPOSE 8080 5005

# Entrypoint is inherited from payara/micro
