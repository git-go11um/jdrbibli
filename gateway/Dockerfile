FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copie du jar
COPY target/gateway-1.0-SNAPSHOT.jar app.jar

# Copie du fichier de configuration
COPY src/main/resources/application.yml /app/application.yml

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
