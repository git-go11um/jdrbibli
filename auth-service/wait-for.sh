FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copie du jar
COPY target/auth-service-1.0-SNAPSHOT.jar app.jar

# Copie le script d'attente depuis la racine du projet
COPY ../wait-for.sh /wait-for.sh

# Rend le script exécutable
RUN chmod +x /wait-for.sh

EXPOSE 8081

ENTRYPOINT ["/wait-for.sh", "mariadb", "3306", "java", "-jar", "app.jar"]