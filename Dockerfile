FROM eclipse-temurin:21-jdk-alpine as builder

RUN apk add --no-cache maven git
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Variables CRÍTICAS para MySQL
ENV MYSQL_URL=${MYSQL_URL}
ENV MYSQL_USER=${MYSQL_USER}
ENV MYSQL_PASSWORD=${MYSQL_PASSWORD}
ENV SERVER_PORT=8080

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q --spider http://localhost:${SERVER_PORT}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
