# syntax=docker/dockerfile:1.7

############################################
# Stage 1 — Build (JDK 25, Gradle cached)
############################################
FROM eclipse-temurin:25-jdk AS builder

# Gradle cache outside workspace for BuildKit sharing
ENV GRADLE_USER_HOME=/gradle
WORKDIR /workspace

# 1) Wrapper and Gradle (rarely change)
COPY --link gradlew gradlew.bat ./
COPY --link gradle ./gradle

# 2) Build config (changes occasionally)
#    Wildcards cover .gradle and .gradle.kts setups
COPY --link build.gradle* settings.gradle* ./

# Warm caches (no build output yet)
RUN --mount=type=cache,target=/gradle,sharing=locked \
    ./gradlew --no-daemon --console=plain dependencies

# 3) Sources (change frequently)
COPY --link src ./src

# Build the runnable Spring Boot jar (no `clean`; avoids cache dir lock issues)
RUN --mount=type=cache,target=/gradle,sharing=locked \
    ./gradlew --no-daemon --console=plain --build-cache bootJar \
 && cp build/libs/*-SNAPSHOT.jar /workspace/app.jar

############################################
# Stage 2 — JRE 25 slimming with jdeps/jlink
############################################
FROM eclipse-temurin:25-jdk AS jrebuilder
WORKDIR /opt

# Bring the fat jar to analyze real deps
COPY --from=builder /workspace/app.jar /opt/app.jar

# Comprehensive module detection for Spring Boot + Tomcat
RUN jdeps \
      --ignore-missing-deps \
      --multi-release 25 \
      --print-module-deps \
      /opt/app.jar \
    | awk '{print $0",jdk.crypto.ec,jdk.localedata,jdk.management,jdk.naming.dns,jdk.security.jgss,java.sql,java.naming,java.desktop,java.instrument,java.management,java.prefs,jdk.unsupported,jdk.charsets"}' > /opt/jre-mods.txt \
 && echo "Using modules: $(cat /opt/jre-mods.txt)"

# Build a minimal JRE 25 image
RUN jlink \
      --add-modules "$(cat /opt/jre-mods.txt)" \
      --output /opt/jre \
      --strip-debug \
      --no-man-pages \
      --no-header-files \
      --compress=2

############################################
# Stage 3 — Runtime (Distroless, non-root)
############################################
# Note: we use distroless *base* and bring our own jlink JRE 25.
# This keeps Java at 25 in runtime while staying distroless/minimal.
FROM gcr.io/distroless/base-debian12:nonroot AS runtime

WORKDIR /app

# OCI annotations (filled by CI at build time)
ARG GIT_COMMIT=unknown
ARG BUILD_DATE
ARG VERSION=0.0.1-SNAPSHOT
LABEL org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.authors="Anass Garoual" \
      org.opencontainers.image.url="https://github.com/AnassGaroual/multi-llm-orchestrator" \
      org.opencontainers.image.source="https://github.com/AnassGaroual/multi-llm-orchestrator" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.revision="${GIT_COMMIT}" \
      org.opencontainers.image.vendor="Anass Garoual" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.title="Multi-LLM Orchestrator" \
      org.opencontainers.image.description="Production-ready AI orchestration — Distroless + custom JRE 25"

# JVM tuning (container-aware GC, silence CDS warning)
ENV _JAVA_OPTIONS="-Xshare:off"
ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Copy custom JRE 25 and the app
COPY --from=jrebuilder --chown=nonroot:nonroot /opt/jre /opt/jre
COPY --from=builder    --chown=nonroot:nonroot /workspace/app.jar /app/app.jar

USER nonroot:nonroot
EXPOSE 8080

# Optional: set Spring profile for containers (to override in compose if needed)
ENV SPRING_PROFILES_ACTIVE=docker

# No shell in distroless, call java directly from jlink JRE
ENTRYPOINT ["/opt/jre/bin/java","-jar","/app/app.jar"]
