# Ginkgoo Core Workspace Service

## Features

### Completed ✅

* Workspace Management 

### In Progress 🚧

## Tech Stack

* Java 21
* Spring Boot 3.x
* Spring Security & Spring Authentication Server
* PostgreSQL
* JWT

## Getting Started

```bash
git clone <repository-url>
cd ginkgoo-workspace
mvn clean install
mvn spring-boot:run
```

## Health Check

Service health can be monitored at:

```bash
GET /health

# Response:
{
    "status": "UP",
}
```

## Configuration

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: ${AUTH_SERVER}/oauth2/jwks
          issuer-uri: ${AUTH_SERVER}

  datasource:
    url: ${POSTGRES_URL}
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}
```

## Requirements

* JDK 21+
* PostgreSQL 14+
* Maven 3.8+

## License

MIT License

---

© 2024 Ginkgoo Core workspace Service

```