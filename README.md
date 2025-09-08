# 📌 Task Manager API  

![Build](https://github.com/maximeacd/task-manager-api/actions/workflows/ci.yml/badge.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/java-17-brightgreen)
![Docker](https://img.shields.io/badge/docker-enabled-blue)

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

docker build -t taskmanager-api .

### Run with Docker Compose (app + PostgreSQL):

docker-compose up --build

--- 

## 📖 Swagger UI

Access API documentation at: http://localhost:8081/swagger-ui.html

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
