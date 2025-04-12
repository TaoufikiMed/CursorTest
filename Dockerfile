FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/secure-api.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"] 