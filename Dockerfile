# ======= BUILD STAGE =======
FROM eclipse-temurin:21-jdk-alpine as builder

# 1. Install dependencies and set up environment
RUN apk add --no-cache \
    bash \
    git \
    openssh-client \
    maven

# 2. Configure working directory and permissions
WORKDIR /app
COPY . .
RUN chmod +x mvnw

# 3. Cache dependencies and build
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests

# ======= RUNTIME STAGE =======
FROM eclipse-temurin:21-jre-alpine

# 4. Install timezone data and configure environment
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone

# 5. Configure runtime variables
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xmx512m -Dserver.port=${SERVER_PORT}"

# 6. Copy built artifact
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 7. Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT}/actuator/health || exit 1

# 8. Entrypoint
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
