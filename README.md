# auth-service

Part of the EQDOM Credit Platform microservices.

- **Port:** 8081
- **Base package:** `com.eqdom.auth`
- **DB schema:** `auth`

## Run

```bash
./mvnw spring-boot:run
```

On Windows without a JDK 21 on PATH, point JAVA_HOME at one first, e.g.:

```bash
JAVA_HOME="/c/Users/<you>/.jdks/corretto-21.0.5" ./mvnw spring-boot:run
```

## Required environment variables
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`

Swagger UI (once implemented): `http://localhost:8081/swagger-ui.html`
