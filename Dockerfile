# Étape 1 : Construire le jar avec Maven
FROM maven:3.9.3-eclipse-temurin-21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers pom.xml et sources
COPY pom.xml .
COPY src ./src

# Construire l'application et créer le jar
RUN mvn clean package -DskipTests

# Étape 2 : Créer l'image finale pour exécuter le jar
FROM eclipse-temurin:21-jre-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le jar depuis l'étape de build
COPY --from=build /app/target/task-manager-api-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port sur lequel Spring Boot tourne
EXPOSE 8081

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]