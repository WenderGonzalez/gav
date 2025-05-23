FROM eclipse-temurin:21-jdk-alpine as builder

# Instala dependencias esenciales
RUN apk add --no-cache bash git openssh-client

# Configura permisos y entorno
WORKDIR /app
COPY . .
RUN chmod +x mvnw  # Añade este comando crucial

# Ejecuta el build
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/gav-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
