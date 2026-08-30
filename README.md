# auth-service

Part of the EQDOM Credit Platform microservices.

- **Port:** 8081
- **Base package:** `com.eqdom.auth`
- **Database:** MySQL/MariaDB (XAMPP), shared database `eqdom_credit`, tables `users`/`roles`/`user_roles`/`refresh_tokens`
- **Migrations:** Flyway, `src/main/resources/db/migration`, history tracked in `flyway_schema_history_auth` (kept separate per service so multiple services can migrate the same shared database independently)

## Prerequisites
- Start XAMPP's MySQL (Apache not required for the backend). Default XAMPP credentials (`root` / no password) work out of the box.
- The `eqdom_credit` database must exist:
  ```sql
  CREATE DATABASE IF NOT EXISTS eqdom_credit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```
  (already created once via `/c/xampp/mysql/bin/mysql.exe -u root -e "..."` during setup)

## Run

```bash
./mvnw spring-boot:run
```

On Windows without a JDK 21 on PATH, point JAVA_HOME at one first, e.g.:

```bash
JAVA_HOME="/c/Users/<you>/.jdks/corretto-21.0.5" ./mvnw spring-boot:run
```

## Required environment variables
- `DB_HOST` (default `localhost`), `DB_PORT` (default `3306`), `DB_NAME` (default `eqdom_credit`), `DB_USERNAME` (default `root`), `DB_PASSWORD` (default empty) — matches a stock XAMPP install
- `JWT_SECRET` — must be overridden outside dev (default is a placeholder, min 32 bytes)

## Endpoints
- `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/refresh`, `POST /api/auth/logout`, `GET /api/auth/me`
- Admin-only: `POST/GET /api/auth/users`, `GET /api/auth/users/{id}`, `PATCH /api/auth/users/{id}/roles`, `PATCH /api/auth/users/{id}/status`

Swagger UI: `http://localhost:8081/swagger-ui.html`
