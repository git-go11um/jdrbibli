FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copie du jar
COPY target/ouvrage-service-3.2.6.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]