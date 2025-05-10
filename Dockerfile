# =============================================================================
# Build stage
# =============================================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy the Maven POM and source code
COPY pom.xml .
COPY src ./src

# Build the application with Maven
# Skip tests in the build stage
RUN mvn clean package -DskipTests

# Unpack the jar to optimize Docker layers
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# =============================================================================
# Runtime stage
# =============================================================================
FROM eclipse-temurin:17-jre-alpine

# Add non-root user for security
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --ingroup appgroup --disabled-password appuser

# Set working directory
WORKDIR /app

# Copy the unpacked application from the build stage
COPY --from=build /app/target/dependency/BOOT-INF/lib /app/lib
COPY --from=build /app/target/dependency/META-INF /app/META-INF
COPY --from=build /app/target/dependency/BOOT-INF/classes /app

# Environment variables with default values
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Expose the application port
EXPOSE ${SERVER_PORT}

# Switch to non-root user for security
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q --spider http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Command to run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -cp app:app/lib/* com.example.todo.TodoSupabaseSpringApplication"]

