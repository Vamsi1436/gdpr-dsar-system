# GDPR DSAR Management System

A full-stack application for logging, tracking, and fulfilling GDPR Data Subject Access Requests (DSARs). Built with Spring Boot (Java 17) and React, this project models the real-world compliance workflow that companies with an EU presence (Meta, Google, LinkedIn, Stripe, Workday, and many financial/insurance firms based in Dublin) must follow when handling requests from data subjects under Articles 15-22 of the GDPR.

## Why this project

GDPR subject access requests carry a strict 30-day legal deadline. Missing that deadline can mean regulatory fines and reputational damage. This system demonstrates the kind of workflow-and-compliance engineering that shows up in enterprise Java roles: a formal state machine for request status, role-based access control, an immutable audit trail, scheduled deadline monitoring with email alerts, and safe handling of sensitive personal documents.

## What it does

An admin logs a new DSAR when a data subject makes a request. The system automatically calculates the legal deadline, assigns the request to a case handler, and tracks it through a defined lifecycle. Every state change, assignment, upload, and export is recorded in an append-only audit log that can never be edited or deleted. A background job checks hourly for requests that are at risk of missing their deadline or are already overdue, and emails the relevant people. Case handlers can upload supporting documents, apply basic redaction to text attachments, and optionally use an LLM to draft the response letter to the data subject before marking the request complete.

## Architecture

**Backend** - Spring Boot 3 (Java 17), Maven, PostgreSQL, Flyway migrations, Spring Security with stateless JWT authentication, Spring Mail, springdoc-openapi for API docs.

**Frontend** - React (Create React App), React Router, Axios, a small context-based auth store using JWT stored in localStorage.

**Core domain model** - `DsarRequest`, `User`, `AuditLog`, `Attachment`. Requests move through a state machine: RECEIVED -> IN_PROGRESS -> PENDING_REVIEW -> COMPLETED, with REJECTED reachable from any non-terminal state. Illegal transitions are rejected by `RequestStateMachine` and surfaced as HTTP 409 errors.

**Roles** - ADMIN (create requests, assign handlers, full access), CASE_HANDLER (work assigned requests, upload evidence, change status), AUDITOR (read-only access to audit logs). Enforced with `@PreAuthorize` at the method level via Spring Method Security.

**Audit trail** - Every meaningful action writes an `AuditLog` row. The repository overrides `delete`/`deleteById` to throw `UnsupportedOperationException`, so the log is append-only at the persistence layer, not just by convention.

**Deadline monitoring** - `DeadlineMonitorScheduler` runs hourly (`@Scheduled`), finds requests within the configurable warning window or past their deadline, and sends alert emails via `EmailService`.

**Redaction** - `RedactionService` applies regex-based masking (emails, phone numbers, long digit sequences) to uploaded text attachments before they are shared, as a lightweight stand-in for a full document-redaction pipeline.

**Optional LLM assist** - `LetterDraftService` can call the Anthropic Messages API to draft a response letter for a request. It is disabled by default (`dsar.letter-draft.enabled=false`) and fails gracefully with a clear message if no API key is configured.

## Project structure

```
gdpr-dsar-system/
backend/
pom.xml
src/main/java/com/dsar/
domain/ JPA entities and enums
repository/ Spring Data repositories
workflow/ status state machine
security/ JWT + Spring Security config
dto/ request/response DTOs
service/ business logic
exception/ centralized error handling
web/ REST controllers
config/ startup data seeding
src/main/resources/
application.yml
db/migration/V1__init.sql
frontend/
src/
api/ Axios client
context/ auth context
components/ Navbar, PrivateRoute, StatusBadge
pages/ Login, Dashboard, RequestList, RequestDetail, NewRequest
docker-compose.yml Postgres + Adminer for local development
.env.example
```

## Running locally

**Prerequisites:** JDK 17+, Maven, Node.js 18+, Docker (for Postgres), or a local PostgreSQL instance.

1. Copy `.env.example` to `.env` and adjust values as needed, or set the equivalent environment variables directly.
2. Start Postgres (and Adminer, optional) with `docker compose up -d`.
3. From `backend/`, run `mvn spring-boot:run`. On first startup, Flyway creates the schema and a default admin user (`admin@dsar.local` / `ChangeMe123!`) is seeded — change this password immediately in any non-local environment.
4. From `frontend/`, run `npm install` then `npm start`. The app expects the backend at `http://localhost:8080/api` by default (see `REACT_APP_API_BASE_URL`).
5. Log in with the seeded admin account, create case handler users as needed, and start logging requests.

## API documentation

Once the backend is running, interactive API docs are available via springdoc at `/swagger-ui.html`.

## Important note on this codebase

This project was written and committed directly through the GitHub web interface without a local build/compile/test step in the loop. The code follows standard Spring Boot and React conventions and has been carefully reviewed, but you should clone the repo and run `mvn compile` / `npm install` locally to catch any issues before relying on it, especially around dependency versions and any small typos that only a compiler or bundler would catch.

## License

MIT
