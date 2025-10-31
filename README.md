# 📌 Task Manager API  

![Build](https://github.com/maximeacd/task-manager-api/actions/workflows/ci.yml/badge.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/java-17-brightgreen)
![Docker](https://img.shields.io/badge/docker-enabled-blue)

---

## REST API allowing users to manage tasks with authentication, due-dates and status tracking.

**Task Manager API** is a production-ready **Spring Boot** REST API for managing users and tasks.

It features JWT authentication, full API documentation via Swagger, unit and integration testing, and containerized deployment with Docker.

---

## ✨ Features

- 🔑 **Secure JWT Authentication** for user login/signup  
- 👤 **User Management** with full CRUD operations  
- ✅ **Task Management** including status tracking and due dates  
- 📖 **Swagger/OpenAPI Documentation** for easy API exploration  
- 🧪 **Unit & Integration Testing** with JUnit, Mockito, MockMvc, and Testcontainers  
- 🐳 **Containerized Deployment** using Docker & Docker Compose 

---

## ⚡ Quick Start

### Local Setup (Maven)

1. Make sure **PostgreSQL** is running locally (default port `5432`).  
2. Update `application.properties` with your database credentials.  
3. Run: 

```bash
mvn clean install
mvn spring-boot:run
```

---

The API will be available at http://localhost:8081

---

## 🐳 Docker Setup

### Build the image:

docker-compose build

### Run with Docker Compose (app + PostgreSQL):

docker-compose up -d

--- 

## 📖 Swagger UI

Access API documentation at: http://localhost:8081/swagger-ui.html

<img width="1478" height="777" alt="image" src="https://github.com/user-attachments/assets/30170010-8bb3-4b3e-983e-80462a6a363f" />
Task controller example

<img width="1165" height="833" alt="image" src="https://github.com/user-attachments/assets/797ea70f-a4d6-4728-b99d-45b18d80108b" />
Creating a task example

---

## 🧪 Testing

### Unit Tests:

mvn test

### Integration Tests with Testcontainers:

Runs a real PostgreSQL container automatically during tests.

---

## 🐙 CI/CD (GitHub Actions)

- Automated build
- Unit & integration test execution
- Docker build validation

Check the badge above to see the latest status 👆

## What I did 

- Designed and implemented the full REST API using Spring Boot with a PostgreSQL backend.
- Developed user authentication and authorization module with JWT.
- Created all CRUD endpoints for task management, including filtering and grouping functionality.
- Wrote unit and integration tests using JUnit and Testcontainers.
- Dockerized the application and created a docker-compose setup for local development.
- Configured CI/CD pipeline with GitHub Actions to run tests on push.
- Optimized API performance and error handling for robust production-ready behavior.
