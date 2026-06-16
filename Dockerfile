# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
COPY core ./core
COPY plugins ./plugins

RUN chmod +x gradlew && ./gradlew :core:api:installDist --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -r grog && useradd -r -g grog grog

COPY --from=build /app/core/api/build/install/api /app

RUN mkdir -p /app/docs /app/logs \
    && chown -R grog:grog /app

USER grog

ENV DB_URL=jdbc:postgresql://postgres:5432/vulnmanager
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres
ENV OPENAPI_OUTPUT_DIR=/app/docs
ENV LOGS_DIR=/app/logs
ENV SERVICE_NAME=vulnerability-manager
ENV ENV=docker
ENV LOGSTASH_ENABLED=true
ENV LOGSTASH_HOST=logstash
ENV LOGSTASH_PORT=5044
ENV METRICS_ENABLED=true

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

ENTRYPOINT ["/app/bin/api"]
