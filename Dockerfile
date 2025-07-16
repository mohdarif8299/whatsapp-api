# Use a lightweight JDK image
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/whatsapp-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
