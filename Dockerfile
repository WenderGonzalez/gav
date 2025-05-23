# ======= BUILD STAGE =======
FROM eclipse-temurin:21-jdk-alpine as builder

# 1. Instalar dependencias esenciales
RUN apk add --no-cache \
    bash \
    git \
    openssh-client \
    maven

# 2. Configurar permisos ANTES de copiar archivos
WORKDIR /app
COPY mvnw ./
COPY .mvn/ .mvn/
RUN chmod +x mvnw

# 3. Copiar el resto y construir
COPY pom.xml .
COPY src ./src
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests

# ======= RUNTIME STAGE =======
FROM eclipse-temurin:21-jre-alpine

# 4. Configuración básica
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone

# 5. Variables de entorno
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xmx512m -Dserver.port=${SERVER_PORT}"

# 6. Copiar artefacto
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 7. Health check y entrypoint
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${
