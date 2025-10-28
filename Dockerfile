# ---- Build stage ----
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /workspace
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew --version
COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon --build-cache

# ---- Runtime stage (distroless) ----
# Image runtime sans shell/apt: plus sûre et légère
FROM gcr.io/distroless/java25-debian12:nonroot
WORKDIR /app

# Silence CDS warning + flags utiles
ENV _JAVA_OPTIONS="-Xshare:off"
ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

COPY --from=builder /workspace/build/libs/*-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
USER nonroot:nonroot
ENTRYPOINT ["java","-jar","/app/app.jar"]
