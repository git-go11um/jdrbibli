# ===== Étape 1 : build Maven =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# 1) Copier le POM parent (à la racine du projet)
COPY pom.xml /app/pom-parent.xml

# 2) Copier le POM du user-service et ses sources
COPY user-service/pom.xml /app/pom.xml
COPY user-service/src /app/src

# 3) Corriger le chemin du parent POM pour que Maven le trouve
RUN sed -i 's#<relativePath>..\/pom.xml</relativePath>#<relativePath>pom-parent.xml</relativePath>#' /app/pom.xml || true

# 4) Compiler le service
RUN mvn -q clean package -DskipTests

# ===== Étape 2 : image finale légère =====
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
