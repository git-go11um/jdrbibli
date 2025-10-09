FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copie du jar
COPY target/user-service-1.0-SNAPSHOT.jar app.jar


EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]