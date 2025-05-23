FROM eclipse-temurin:21-jdk-alpine as builder
RUN apk add --no-cache bash git openssh-client maven
WORKDIR /app
COPY mvnw .
COPY .mvn/wrapper/maven-wrapper.properties .mvn/wrapper/
RUN chmod +x mvnw
COPY pom.xml .
COPY src ./src
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java -Xmx512m -Dserver.port=8080 -jar app.jar"]
