# Build stage
FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app

# Copy build files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built artifact
COPY --from=builder /app/target/*.jar app.jar

# Expose port and run
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
