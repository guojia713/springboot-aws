# Spring Boot AWS — REST API Starter

A production-ready Spring Boot 3 REST API configured for AWS deployment.

## Tech Stack
- **Java 21** + Spring Boot 3.2
- **PostgreSQL** (RDS in production, local Docker for dev)
- **AWS Elastic Beanstalk** for hosting
- **AWS Parameter Store** for secrets
- **Docker** + **GitHub Actions** for CI/CD

---

## 🚀 Quick Start (Local)

### Option A — Run with local PostgreSQL (Docker)
```bash
# Start PostgreSQL + app together
docker compose up -d

# App is available at:
curl http://localhost:8080/health
```

### Option B — Run with Maven (requires local PostgreSQL)
```bash
# Start PostgreSQL locally first, then:
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 🧪 Running Tests
```bash
./mvnw test
# Uses H2 in-memory DB — no PostgreSQL needed
```

---

## 📦 Build JAR
```bash
./mvnw clean package -DskipTests
# → target/app-0.0.1-SNAPSHOT.jar
```

---

## ☁️ Deploy to AWS

### 1. First-time setup
1. Create RDS PostgreSQL instance on AWS
2. Create Elastic Beanstalk application (Java platform)
3. Add environment variables in Beanstalk:
   ```
   DB_URL        = jdbc:postgresql://<rds-endpoint>:5432/appdb
   DB_USERNAME   = youruser
   DB_PASSWORD   = yourpassword
   SERVER_PORT   = 5000
   ```

### 2. Add GitHub Secrets
Go to your GitHub repo → Settings → Secrets → Actions:
```
AWS_ACCESS_KEY_ID      → your IAM access key
AWS_SECRET_ACCESS_KEY  → your IAM secret key
AWS_REGION             → e.g. us-east-1
EB_APPLICATION_NAME    → your Beanstalk app name
EB_ENVIRONMENT_NAME    → your Beanstalk environment name
```

### 3. Deploy
```bash
git push origin main
# GitHub Actions automatically: builds → tests → deploys to AWS
```

---

## 🔌 API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/health` | Health check (used by AWS) |
| GET | `/api/v1/products` | List all products |
| GET | `/api/v1/products?search=laptop` | Search products by name |
| GET | `/api/v1/products/{id}` | Get product by ID |
| POST | `/api/v1/products` | Create product |
| PUT | `/api/v1/products/{id}` | Update product |
| DELETE | `/api/v1/products/{id}` | Delete product |

### Example: Create a product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Laptop", "description": "Great laptop", "price": 999.99}'
```

---

## 📁 Project Structure
```
src/
├── main/
│   ├── java/com/example/app/
│   │   ├── Application.java           ← entry point
│   │   ├── controller/
│   │   │   ├── ProductController.java ← REST endpoints
│   │   │   └── HealthController.java  ← AWS health check
│   │   ├── service/
│   │   │   └── ProductService.java    ← business logic
│   │   ├── repository/
│   │   │   └── ProductRepository.java ← DB queries
│   │   ├── entity/
│   │   │   └── Product.java           ← JPA entity
│   │   └── config/
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.properties       ← production (AWS)
│       ├── application-local.properties ← local dev
│       └── application-test.properties  ← tests (H2)
├── test/
│   └── java/com/example/app/
│       ├── ProductServiceTest.java      ← unit tests
│       └── ProductControllerIntegrationTest.java
Dockerfile                               ← multi-stage build
docker-compose.yml                       ← local dev environment
.github/workflows/deploy.yml            ← CI/CD pipeline
```
