## Product Service (Spring Boot 3)

Product Management REST API built with **Spring Boot **, **JWT**, and **Spring Data JPA / MongoDB** using a clean layered architecture and common design patterns.

This service exposes endpoints to **authenticate**, then **create, read, update, and delete products**, with optional filtering by inventory status and category.

---

## Tech stack

- **Language**: Java 17  
- **Framework**: Spring Boot 3.2.3  
- **Persistence**:
  - PostgreSQL via Spring Data JPA (`sql` profile)
  - MongoDB via Spring Data MongoDB (`mongo` profile, via adapters)
- **Security**: Spring Security + JWT 
- **Documentation**: springdoc-openapi (Swagger UI)

---

## How to run (SQL profile with PostgreSQL)

### Option A — Local PostgreSQL (no Docker)

1. **Create database**

```sql
CREATE DATABASE productdb;
```

2. **Configure PostgreSQL connection**

`src/main/resources/application-sql.properties` (already provided):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/productdb
spring.datasource.username=postgres
spring.datasource.password=postgres123
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

3. **Enable the `sql` profile**

Run with Maven:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=sql
```

or with the jar:

```bash
mvn clean package
java -jar target/product-0.0.1-SNAPSHOT.jar --spring.profiles.active=sql
```

### Option B — PostgreSQL with Docker

1. **Start PostgreSQL container**

From the project root:

```bash
docker compose up -d postgres
```

This will start a Postgres 16 container with:

- **Database**: `productdb`
- **User**: `postgres`
- **Password**: `postgres123`
- **Port**: `5432` (mapped to localhost)

2. **Run the application with `sql` profile**

Use the same commands as above (Maven or jar) — the JDBC URL in `application-sql.properties` already points to `localhost:5432/productdb`.

### Open Swagger UI

Once started, open:

```text
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI definition is also available at:

```text
http://localhost:8080/api-docs
```

---

## How to run (Mongo profile with MongoDB)

### Option A — Local MongoDB (no Docker)

1. **Start MongoDB** on your machine (default port `27017`) and create a database:

```bash
mongosh
> use productdb
```

2. **Mongo profile configuration**

`src/main/resources/application-mongo.properties`:

```properties
spring.data.mongodb.uri=mongodb://root:mongo123@localhost:27017/productdb?authSource=admin
spring.data.mongodb.database=productdb
spring.data.mongodb.auto-index-creation=true

spring.autoconfigure.exclude=\
  org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
  org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
  org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
```

Adjust `spring.data.mongodb.uri` if your local Mongo instance uses different credentials.

3. **Run the application with `mongo` profile**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mongo
```

or:

```bash
mvn clean package
java -jar target/product-0.0.1-SNAPSHOT.jar --spring.profiles.active=mongo
```

### Option B — MongoDB with Docker

1. **Start MongoDB + Mongo Express**

From the project root:

```bash
docker compose up -d mongodb mongo-express
```

This will start:

- **MongoDB** (`mongodb` service) with:
  - Root user: `root`
  - Root password: `mongo123`
  - Default DB: `productdb`
- **Mongo Express UI** on `http://localhost:8081` (user `admin` / password `admin123` by default).

2. **Run the application with `mongo` profile**

Use the same commands as above (Maven or jar). The URI in `application-mongo.properties` is already compatible with the Docker setup.

---

## Authentication & security

- Authentication is **stateless** and uses **JWT bearer tokens**.
- Public endpoints:
  - `/api/v1/auth/**`
  - Swagger/OpenAPI: `/swagger-ui/**`, `/swagger-ui.html`, `/api-docs/**`
- All other endpoints require a valid `Authorization: Bearer <token>` header.

### Login flow

1. **Login**:

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

2. **Response**:

```json
{
  "token": "<jwt-token>",
  "expiresIn": 3600000
}
```

3. **Use token** in protected endpoints:

```http
Authorization: Bearer <jwt-token>
```

JWT settings (expected) in properties:

```properties
jwt.secret=BASE64_ENCODED_SECRET_KEY
jwt.expiration=3600000
```

> The secret must be a Base64-encoded string long enough for HMAC-SHA key creation.

---

## Main endpoints

### Products (`/api/v1/products`)

All product endpoints require authentication.

- **Create product**

```http
POST /api/v1/products
Content-Type: application/json
Authorization: Bearer <token>
```

Body (simplified):

```json
{
  "code": "P-001",
  "name": "Laptop",
  "description": "High-end laptop",
  "image": "data:image/png;base64,....",
  "category": "ELECTRONICS",
  "price": 1500.0,
  "quantity": 10,
  "shellId": 1,
  "inventoryStatus": "INSTOCK",
  "rating": 4.5
}
```

- **Get all products** (optional filters):

```http
GET /api/v1/products?inventoryStatus=INSTOCK&category=ELECTRONICS
Authorization: Bearer <token>
```

Both `inventoryStatus` and `category` are optional query params.

- **Get by id**

```http
GET /api/v1/products/{id}
Authorization: Bearer <token>
```

- **Update**

```http
PUT /api/v1/products/{id}
Authorization: Bearer <token>
```

Body is the same shape as the create request.

- **Delete**

```http
DELETE /api/v1/products/{id}
Authorization: Bearer <token>
```

---



## Running tests

Currently there are no automated tests. You can start by adding:

- Unit tests for:
  - `ProductServiceImpl`
  - `ValidatedProductService`
  - `JwtService`
- Web tests for controllers using `spring-boot-starter-test` and `spring-security-test`.

Run all tests:

```bash
mvn test
```

---


```

