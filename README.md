# vibehopper

vibehopper is a monorepo for the VibeHopper event discovery app.

```text
vibehopper/
  vibehopper_be/  Spring Boot API with PostgreSQL + PostGIS
  vibehopper_fe/  frontend placeholder
  scripts/        repository-level verification commands
```

## Current Status

- `vibehopper_be/` contains the active backend.
- `vibehopper_fe/` has not been scaffolded yet.
- Backend history is preserved from the original VibeHopper backend repository.

## Verify

Run the root gate before opening a pull request:

```bash
./scripts/verify.sh
```

Backend-only:

```bash
cd vibehopper_be
./scripts/verify.sh
```

## Local Development

Backend setup and API details live in [vibehopper_be/README.md](vibehopper_be/README.md).

The frontend should be built after the backend OpenAPI contract is stable enough to generate a client from `/v3/api-docs`.
