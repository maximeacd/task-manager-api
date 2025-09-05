# 📌 Task Manager API  

![Build](https://github.com/maximeacd/task-manager-api/actions/workflows/ci.yml/badge.svg)

Une API REST développée avec **Spring Boot** permettant de gérer des utilisateurs et des tâches.  
Elle inclut la sécurité avec JWT, la documentation via Swagger/OpenAPI, et un déploiement containerisé avec Docker.

---

## ✨ Features

- 🔑 **Authentification JWT** (inscription + login)  
- 👤 **Gestion des utilisateurs** (CRUD)  
- ✅ **Gestion des tâches** (CRUD + statut + date d’échéance)  
- 📖 **Documentation API** avec Swagger UI  
- 🧪 **Tests unitaires** (JUnit + Mockito) et **tests d’intégration** (MockMvc, Testcontainers)  
- 🐳 **Docker & Docker Compose** pour exécuter l’app et PostgreSQL  

---

## ⚡ Lancer en local

### Avec Maven (sans Docker)

1. Assure-toi d’avoir **PostgreSQL** qui tourne en local (port `5432` par défaut).  
2. Configure `application.properties` avec tes identifiants DB.  
3. Lance :  

```bash
mvn clean install
mvn spring-boot:run

---

L’API sera dispo sur http://localhost:8081.

---

## 🐳 Avec Docker

### Construis l’image :

docker build -t taskmanager-api .

### Lance avec Docker Compose (app + PostgreSQL) :

docker-compose up --build

---

🛠️ Endpoints principaux

Exemple avec curl :

🔑 Auth

Signup :

curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"max","password":"1234"}'

Login :

curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"max","password":"1234"}'

✅ Tâches

Lister toutes les tâches :

curl -X GET http://localhost:8080/tasks -H "Authorization: Bearer <TOKEN>"

Créer une tâche :

curl -X POST http://localhost:8080/tasks \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Faire les courses","description":"Acheter du lait","status":"OPEN"}'

📖 Swagger UI

Une fois l’app démarrée, accède à la doc API ici : http://localhost:8081/swagger-ui.html

🧪 Tests

Unitaires : mvn test

Intégration avec Testcontainers (PostgreSQL) : Lance automatiquement un vrai container PostgreSQL pendant les tests.

🐙 CI/CD (GitHub Actions)

- Build du projet
- Lancement des tests
- Vérification que le Docker build passe

Voir le badge en haut 👆
