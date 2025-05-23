# ===== BUILD STAGE =====
FROM eclipse-temurin:21-jdk-alpine as builder

# 1. Instalar dependencias
RUN apk add --no-cache \
    bash \
    git \
    openssh-client \
    maven

# 2. Configurar entorno
WORKDIR /app

# 3. Copiar archivos del wrapper primero
COPY mvnw .
COPY .mvn/wrapper/maven-wrapper.properties .mvn/wrapper/
RUN chmod +x mvnw  # Permisos de ejecución

# 4. Copiar el resto
COPY pom.xml .
COPY src ./src

# 5. Build
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests

# ===== RUNTIME STAGE =====
FROM eclipse-temurin:21-jre-alpine

# 6. Configuración de tiempo
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone

# 7. Variables de entorno
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xmx512m -Dserver.port=${SERVER_PORT}"

# 8. Copiar artefacto
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 9. Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT}/actuator/health || exit 1

# 10. Entrypoint
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
