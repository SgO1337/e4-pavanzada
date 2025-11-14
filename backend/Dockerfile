# Backend Dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8081", "--server.address=0.0.0.0"]