# syntax=docker/dockerfile:1.4

# ============================================
# Build stage - Multi-platform compatible
# ============================================
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /workspace

# Copy Gradle wrapper and config files (for layer caching)
COPY --link gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY --link gradle ./gradle

# Download dependencies (cached layer if build.gradle unchanged)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon

# Copy source code
COPY --link src ./src

# Build application (with Gradle cache mount)
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/workspace/build \
    ./gradlew clean bootJar -x test --no-daemon --build-cache && \
    cp build/libs/*-SNAPSHOT.jar app.jar

# ============================================
# Runtime stage - Distroless for security
# ============================================
FROM gcr.io/distroless/java25-debian12:nonroot

WORKDIR /app

# JVM optimizations
ENV _JAVA_OPTIONS="-Xshare:off"
ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -XX:+UseContainerSupport"

# OCI labels for metadata
LABEL org.opencontainers.image.source="https://github.com/$GITHUB_REPOSITORY"
LABEL org.opencontainers.image.description="Multi-LLM Orchestrator - Spring Boot application"
LABEL org.opencontainers.image.licenses="MIT"

# Copy built artifact
COPY --from=builder --chown=nonroot:nonroot /workspace/app.jar /app/app.jar

# Health check endpoint (if you have /actuator/health)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD ["/usr/bin/wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]

EXPOSE 8080

USER nonroot:nonroot

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
