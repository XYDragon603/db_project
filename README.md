# MedMinder

MedMinder is a full-stack medication schedule and refill tracking project built with Spring Boot, React, and PostgreSQL.

## Prerequisites

- Java 21
- Maven 3.9+
- Node.js 24+
- PostgreSQL 15+

## Required Environment Variables

Copy `.env.example` and fill in safe local or deployment values.

Backend:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `DB_INIT_MODE`
- `FRONTEND_ORIGIN`

Frontend:

- `VITE_API_BASE_URL`
- `VITE_ENABLE_MOCK_FALLBACK`

## Database Setup

Run the SQL files in this order:

1. `database/schema.sql`
2. `database/indexes.sql`
3. `database/seed.sql`
4. `database/fix-sequences.sql` if you already loaded seed data into an existing Render database and generated IDs are out of sync

For local initialization through Spring Boot:

```bash
DB_INIT_MODE=always
```

For Render PostgreSQL:

- keep `DB_INIT_MODE=never`
- run the SQL files directly against the Render database
- set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`

The backend also accepts a Render-style `postgresql://...` URL in `SPRING_DATASOURCE_URL`.

## Run the Backend

```bash
cd backend
mvn spring-boot:run
```

Default backend URL:

```text
http://localhost:8080
```

Health check:

```text
GET http://localhost:8080/actuator/health
```

## Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

Default frontend URL:

```text
http://localhost:5173
```

If PowerShell blocks `npm.ps1`, run the command through `cmd` instead:

```bash
cmd /c "npm run --prefix frontend dev"
```

## Demo Accounts

- User: `emily@example.com`
- Caregiver: `alex.caregiver@example.com`
- Admin: `admin@example.com`
- Password: `password`

## Build and Test Commands

Frontend:

```bash
npm run --prefix frontend check
npm run --prefix frontend build
```

Backend:

```bash
mvn test -f backend/pom.xml
```

## Render Deployment

This repository includes a root-level [`render.yaml`](render.yaml) Blueprint.

Recommended deploy order:

1. Push the repository to GitHub.
2. In Render, create services from `render.yaml` or connect the repo manually.
3. In the Render PostgreSQL database, run:
   - `database/schema.sql`
   - `database/indexes.sql`
   - `database/seed.sql`
   - `database/fix-sequences.sql` if needed
4. Set the prompted Blueprint values:
   - `FRONTEND_ORIGIN`
   - `VITE_API_BASE_URL`
5. Keep:
   - `DB_INIT_MODE=never`
   - `VITE_ENABLE_MOCK_FALLBACK=false`

Expected deployed values:

- `FRONTEND_ORIGIN=https://<your-frontend>.onrender.com`
- `VITE_API_BASE_URL=https://<your-backend>.onrender.com/api`

The Blueprint also includes a rewrite rule so React routes like `/admin/roles` and `/user/reports` work correctly on a static site refresh.

## Final Checks

Before submission or deployment:

```bash
npm run --prefix frontend check
npm run --prefix frontend build
mvn test -f backend/pom.xml
```
