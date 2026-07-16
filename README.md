# SocialSync API

SocialSync API is a Spring Boot backend for users and social events. It provides JWT authentication, user administration, and event CRUD backed by MongoDB.

## Requirements

- Java 17
- Gradle wrapper included in this repo
- MongoDB connection configured through Spring Boot configuration
- `JWT_SECRET_KEY` set to a Base64-encoded HMAC key of at least 32 bytes

On macOS, if Java 21 is your default JDK, run commands with Java 17:

```bash
JAVA_HOME="$(/usr/libexec/java_home -v 17)" ./gradlew test
```

## Configuration

The application reads standard Spring Boot configuration. Minimum local configuration:

```bash
export JWT_SECRET_KEY="<base64-encoded-32-byte-key>"
```

MongoDB settings can be supplied in `application-local.yml`, environment variables, or your deployment secret/configuration system.

## Run And Verify

```bash
./gradlew bootRun
./scripts/verify.sh
```

Useful individual checks:

```bash
./gradlew spotlessCheck
./gradlew test
```

## API Overview

Authentication:

- `POST /api/auth/register` creates a user account and returns a JWT.
- `POST /api/auth/authenticate` authenticates an existing user and returns a JWT.

Events:

- `GET /api/event/events` lists events publicly.
- `GET /api/event/{id}` reads one event publicly.
- `POST /api/event/insert` creates an event for the authenticated user.
- `PUT /api/event/edit/{id}` updates an event for its owner or an admin.
- `DELETE /api/event/del/{id}` deletes an event for its owner or an admin.

Users:

- `GET /api/user/me` returns the authenticated user.
- Other `/api/user/**` routes are admin-only.

Protected endpoints require:

```http
Authorization: Bearer <jwt>
```

## Development Notes

- The project targets Java 17. Gradle 8.1.1 does not run correctly on Java 21 in this checkout, so use Java 17 for local commands.
- Formatting is enforced with Spotless.
- `scripts/verify.sh` is the local quality gate and should pass before opening a pull request.
