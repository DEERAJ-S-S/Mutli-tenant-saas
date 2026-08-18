# Multi-Tenant SaaS Application Architecture

Java · Spring Boot · PostgreSQL · Hibernate · Docker

A multi-tenant SaaS backend that isolates each tenant's data using a
**PostgreSQL schema-per-tenant** strategy, resolved dynamically at
runtime by a custom Hibernate `MultiTenantConnectionProvider`. Access
is secured end-to-end with **JWT authentication** and **role-based
access control (RBAC)**.

## Features

- Dynamic schema-based multi-tenancy (Hibernate `SCHEMA` strategy)
- JWT issuance/validation (`io.jsonwebtoken`) with tenant + role claims
- RBAC via Spring Security `@PreAuthorize` (`ADMIN`, `TENANT_MANAGER`, `USER`)
- Tenant context propagated per-request via `ThreadLocal` + servlet filter
- Dockerized app + Postgres via `docker-compose`
- GitHub Actions CI/CD pipeline (build → test → Docker image push)

## Project structure

```
multi-tenant-saas/
├── src/main/java/com/example/saas/
│   ├── config/       # Security, Tenant/Hibernate, filter registration
│   ├── controller/   # REST endpoints (auth, projects)
│   ├── dto/          # Request/response objects
│   ├── entity/       # JPA entities (AppUser, Project)
│   ├── filter/        # Tenant header resolution filter
│   ├── repository/   # Spring Data JPA repositories
│   ├── security/     # JwtUtil, JwtAuthFilter
│   └── tenant/       # TenantContext, connection provider, resolver
├── src/main/resources/application.yml
├── db/init.sql       # Seeds two demo tenant schemas + a demo user
├── Dockerfile
├── docker-compose.yml
└── .github/workflows/ci-cd.yml
```

## Run it locally

### Option A — Docker Compose (recommended, zero local setup)

Requires Docker Desktop / Docker Engine + Compose installed.

```bash
cd multi-tenant-saas
docker compose up --build
```

This spins up Postgres (seeded with two demo tenant schemas,
`tenant_acme` and `tenant_globex`) and the Spring Boot app on
`http://localhost:8080`.

Demo login (password for both is `password123`):

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@acme.com","password":"password123"}'
```

Use the returned JWT to call tenant-scoped endpoints:

```bash
TOKEN="paste-jwt-here"
curl http://localhost:8080/api/projects \
  -H "Authorization: Bearer $TOKEN"
```

Requests from `admin@acme.com` only ever see rows in the
`tenant_acme` schema; `user@globex.com` only sees `tenant_globex`.

### Option B — Run locally with Maven + a local Postgres

1. Install PostgreSQL 16 locally and create a database:
   ```sql
   CREATE DATABASE saas_db;
   CREATE USER saas_user WITH PASSWORD 'saas_password';
   GRANT ALL PRIVILEGES ON DATABASE saas_db TO saas_user;
   ```
2. Run the seed script: `psql -U saas_user -d saas_db -f db/init.sql`
3. Build and run:
   ```bash
   mvn clean package
   java -jar target/multi-tenant-saas-1.0.0.jar
   ```
   (or `mvn spring-boot:run` for hot iteration)

Environment variables you can override: `DB_HOST`, `DB_PORT`,
`DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`.

## Running tests

```bash
mvn test
```

## Push this project to GitHub

From inside the `multi-tenant-saas` folder:

```bash
git init
git add .
git commit -m "Initial commit: multi-tenant SaaS backend"

# Create the repo on GitHub first (via github.com or `gh repo create`), then:
git branch -M main
git remote add origin https://github.com/<your-username>/multi-tenant-saas.git
git push -u origin main
```

Using the GitHub CLI instead:

```bash
gh repo create multi-tenant-saas --public --source=. --remote=origin --push
```

### Enabling the included CI/CD pipeline

The workflow at `.github/workflows/ci-cd.yml` builds and tests on every
push/PR, and pushes a Docker image to Docker Hub on pushes to `main`.
To enable the Docker publish step, add two repository secrets under
**Settings → Secrets and variables → Actions**:

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN` (a Docker Hub access token, not your password)

If you don't want the Docker push step, delete the `docker-build-push`
job from the workflow file — the build/test job will still run.
# Mutli-tenant-saas
