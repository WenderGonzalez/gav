FROM eclipse-temurin:21-jdk-alpine as builder

# 1. Instalar dependencias necesarias
RUN apk add --no-cache \
    bash \
    git \
    openssh-client \
    maven

# 2. Configurar directorio de trabajo
WORKDIR /app

# 3. Copiar PRIMERO los archivos esenciales del wrapper
COPY mvnw ./
COPY .mvn/wrapper/maven-wrapper.properties .mvn/wrapper/
RUN chmod +x mvnw  # Dar permisos de ejecución

# 4. Copiar el resto de archivos
COPY pom.xml .
COPY src ./src

# 5. Ejecutar Maven
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
