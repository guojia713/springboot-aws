# =============================================
# STAGE 1 — Build the JAR
# =============================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (layer caching — only re-downloads
# dependencies when pom.xml changes, not on every code change)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -q

# Now copy source and build
COPY src src
RUN ./mvnw clean package -DskipTests -q

# =============================================
# STAGE 2 — Extract layers for faster restarts
# =============================================
FROM eclipse-temurin:21-jdk-alpine AS extractor
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# =============================================
# STAGE 3 — Final lightweight runtime image
# =============================================
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user for security (never run as root in production!)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

# Copy extracted layers in order (least → most frequently changed)
# This maximises Docker layer caching on re-deploys
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./

USER appuser

# AWS Elastic Beanstalk expects port 5000
EXPOSE 5000

# Tuned JVM flags for containers:
# -XX:+UseContainerSupport  → respects Docker memory limits
# -XX:MaxRAMPercentage=75   → use 75% of container RAM for heap
# -Dspring.profiles.active  → set to 'prod' in production
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
