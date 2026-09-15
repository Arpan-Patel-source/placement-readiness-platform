# ─────────────────────────────────────────────────────────────────────────────
# Root Dockerfile for Render Deployment (PlacementAI Backend)
# ─────────────────────────────────────────────────────────────────────────────

# Stage 1: Build JAR
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper & POM from backend directory
COPY backend/.mvn ./backend/.mvn
COPY backend/mvnw backend/pom.xml ./backend/
RUN chmod +x ./backend/mvnw

WORKDIR /app/backend
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build package
COPY backend/src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal Production Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root group and user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Create data directory for H2/file storage fallback
RUN mkdir -p /app/data && chown -R appuser:appgroup /app

USER appuser

# Copy executable jar from builder stage
COPY --from=builder --chown=appuser:appgroup /app/backend/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

# Run Spring Boot application, dynamically passing the PORT allocated by Render
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT:-8080} -jar app.jar"]
