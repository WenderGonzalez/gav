FROM eclipse-temurin:21-jdk-alpine as builder

# 1. Instalar dependencias necesarias
RUN apk add --no-cache \
    bash \
    git \
    openssh-client \
    maven

# 2. Configurar permisos ANTES de copiar
WORKDIR /app
COPY mvnw .mvn/wrapper/maven-wrapper.properties ./
RUN chmod +x mvnw  # ESTE ES EL CAMBIO CLAVE

# 3. Copiar el resto y construir
COPY pom.xml .
COPY src ./src
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
