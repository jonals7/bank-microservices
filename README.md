# Bank Microservices

REST API built with Spring Boot + WebFlux + R2DBC, split into two microservices following clean architecture principles.

## Architecture

```
┌─────────────────────┐    REST    ┌──────────────────────┐
│  customer-service   │ ◄────────► │   account-service    │
│  Port: 8080         │            │   Port: 8081         │
│                     │            │                      │
│  - Persona          │            │  - Cuenta            │
│  - Cliente          │            │  - Movimiento        │
│  /api/v1/customers  │            │  /api/v1/accounts    │
│                     │            │  /api/v1/movements   │
│                     │            │  /reports/{id}       │
└─────────────────────┘            └──────────────────────┘
        │                                    │
        ▼                                    ▼
 postgres-customer                   postgres-account
   (Port 5432)                         (Port 5433)
```

## Tech Stack

- Java 21
- Spring Boot 3.4.3
- Spring WebFlux (reactive, non-blocking)
- Spring Data R2DBC (reactive database access)
- PostgreSQL 16
- Lombok
- Springdoc OpenAPI 2.8.5
- Docker + Docker Compose

## Prerequisites

- Docker Desktop installed and running
- Ports 8080, 8081, 5432, 5433 available

## Running the Project

Clone the repository and run from the root folder:

```bash
git clone <your-repo-url>
cd ntt-test

# Build images and start all containers
docker-compose up --build
```

First build takes a few minutes. Subsequent runs are faster:

```bash
docker-compose up
```

To stop:

```bash
docker-compose down

# Stop and delete all data (reset databases)
docker-compose down -v
```

## API Documentation (Swagger UI)

Once running, open in your browser:

| Service | Swagger UI | OpenAPI JSON |
|---|---|---|
| customer-service | http://localhost:8080/swagger-ui.html | http://localhost:8080/api-docs |
| account-service | http://localhost:8081/swagger-ui.html | http://localhost:8081/api-docs |

## Endpoints

### customer-service (port 8080)

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/v1/customers | Get all customers |
| GET | /api/v1/customers/{id} | Get customer by ID |
| POST | /api/v1/customers | Create customer |
| PUT | /api/v1/customers/{id} | Update customer |
| DELETE | /api/v1/customers/{id} | Delete customer |

### account-service (port 8081)

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/v1/accounts | Get all accounts |
| GET | /api/v1/accounts/{id} | Get account by ID |
| POST | /api/v1/accounts | Create account |
| PUT | /api/v1/accounts/{id} | Update account |
| DELETE | /api/v1/accounts/{id} | Delete account |
| GET | /api/v1/movements | Get all movements |
| GET | /api/v1/movements/{id} | Get movement by ID |
| POST | /api/v1/movements | Register movement |
| PUT | /api/v1/movements/{id} | Update movement |
| DELETE | /api/v1/movements/{id} | Delete movement |
| GET | /reports/{clientId}?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd | Account statement |

## Business Rules

- Movement amount must be greater than zero
- DEBIT movements subtract from available balance
- CREDIT movements add to available balance
- If balance is insufficient for a DEBIT, the API returns: `"Saldo no disponible"`
- Each microservice owns its own database (no shared DB)
- account-service validates customer existence via REST call to customer-service

## Running Tests

```bash
# customer-service tests
cd customer-service
mvn test

# account-service tests
cd account-service
mvn test
```

## Deliverables

- `BaseDatos.sql` - Full database schema and sample data
- `BankMicroservices.postman_collection.json` - Postman collection
- `openapi-customer-service.yaml` - OpenAPI spec for customer-service
- `openapi-account-service.yaml` - OpenAPI spec for account-service
