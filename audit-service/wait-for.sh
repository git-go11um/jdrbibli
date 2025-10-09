FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copie du jar
COPY target/auth-service-1.0-SNAPSHOT.jar app.jar

# Copie du script depuis la racine du projet
COPY ../../wait-for.sh .

EXPOSE 8085

ENTRYPOINT ["./wait-for.sh", "mariadb:3306", "--", "java", "-jar", "app.jar"]
