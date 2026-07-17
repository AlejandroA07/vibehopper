# VibeHopper Backend

Spring Boot 3 REST API with PostgreSQL + PostGIS.

## Prerequisites

- Java 17+
- Gradle (or use `./gradlew`)

## Running the app

### Option A — Docker (PostgreSQL in container)

**Requires:** Docker Desktop running.

```bash
cd vibehopper_be
docker compose up -d
./gradlew bootRun --args='--spring.profiles.active=local'
```

Stop the container when done:

```bash
cd vibehopper_be
docker compose down
```

---

### Option B — Local PostgreSQL (no Docker)

**Requires:** PostgreSQL 16+ with the PostGIS extension installed.
On Windows, install PostGIS via Stack Builder after installing PostgreSQL.

**One-time database setup** — run as the `postgres` superuser:

```sql
CREATE USER vibehopper WITH PASSWORD 'vibehopper';
CREATE DATABASE vibehopperdb OWNER vibehopper;

\c vibehopperdb

CREATE EXTENSION IF NOT EXISTS postgis;
GRANT ALL PRIVILEGES ON DATABASE vibehopperdb TO vibehopper;
GRANT ALL ON SCHEMA public TO vibehopper;
```

From the command line (adjust path to your PostgreSQL install):

```bash
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE USER vibehopper WITH PASSWORD 'vibehopper';"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE DATABASE vibehopperdb OWNER vibehopper;"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -d vibehopperdb -c "CREATE EXTENSION IF NOT EXISTS postgis;"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -d vibehopperdb -c "GRANT ALL PRIVILEGES ON DATABASE vibehopperdb TO vibehopper;"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -d vibehopperdb -c "GRANT ALL ON SCHEMA public TO vibehopper;"
```

**Run the app:**

```bash
cd vibehopper_be
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

## The `local` Spring profile

Activating `local` enables `application-local.yml` on top of the base config:

- SQL logging enabled (`show-sql: true`)
- Debug logging for `com.vibehopper`

Base config (`application.yml`) defaults are sufficient for both Docker and local development — both connect to `localhost:5432/vibehopperdb`.

---

## Environment variables

Database variables have dev-safe defaults. Set `JWT_SECRET_KEY` via OS environment variables or Windows System Environment Variables before running the app.

| Variable | Default | Description |
|---|---|---|
| `POSTGRES_URL` | `jdbc:postgresql://localhost:5432/vibehopperdb` | JDBC connection URL |
| `POSTGRES_USER` | `vibehopper` | Database user |
| `POSTGRES_PASSWORD` | `vibehopper` | Database password |
| `JWT_SECRET_KEY` | *(required)* | Base64-encoded JWT signing secret |
| `JWT_EXPIRATION` | `24h` | JWT token lifetime |
| `FRONTEND_CORS_ORIGINS` | `http://localhost:5173` | Allowed CORS origins |

---

## API docs

Swagger UI available at `http://localhost:8080/swagger-ui/index.html` when the app is running.

## Verify

From the repository root:

```bash
./scripts/verify.sh
```

From `vibehopper_be/`:

```bash
./scripts/verify.sh
```
