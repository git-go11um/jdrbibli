# Utilise une image Java avec OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine

# Définit le répertoire de travail dans le container
WORKDIR /app

# Copie le jar du service dans le container
COPY target/auth-service-1.0-SNAPSHOT.jar app.jar

# Expose le port sur lequel ton service tourne
EXPOSE 8081

# Commande pour lancer le service
ENTRYPOINT ["java", "-jar", "app.jar"]
