FROM eclipse-temurin:17-jre

WORKDIR /app

COPY connector-*.jar app.jar
COPY .env .env

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]