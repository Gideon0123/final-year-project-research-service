# Research-Service (Final Year Project)

This repository contains the **Research-Service** backend module for the final-year project:

> **Design and implementation of a web based Academic Digital Repository and Collaboration System**

The actual Spring Boot service code is in:

- `/RESEARCH-SERVICE`

---

## 1) What this service is responsible for

`RESEARCH-SERVICE` is responsible for research paper and research category domain operations, including:

- Creating, updating, retrieving, searching, submitting, reviewing, publishing, and deleting research papers
- Uploading/downloading/deleting research paper files through a storage abstraction (implemented with MinIO)
- Managing research categories
- Returning published research candidates
- Applying service-level business rules around ownership, role-based actions, status transitions, and visibility
- Exposing internal status update and summary/file metadata endpoints used by other services

### What this service does **not** own

This repository does **not** contain:

- API Gateway implementation (separate repository):
  - https://github.com/Gideon0123/final-year-project-api-gateway
- Discovery server (Eureka server implementation)
- Spring Cloud Config Server implementation
- Authentication/JWT verification logic
- Auth/user management service implementation (`AUTH-SERVICE` is external)
- Docker, Docker Compose, Kubernetes, Helm, or deployment manifests

---

## 2) Architecture and service relationships

### Service identity and cloud integration

From `RESEARCH-SERVICE/src/main/resources/application.properties`:

- `spring.application.name=RESEARCH-SERVICE`
- `spring.config.import=optional:configserver:http://localhost:8888`
- `eureka.client.service-url.defaultZone=http://localhost:8761/eureka`

This means:

- Service name is **RESEARCH-SERVICE**
- It is configured as a Eureka client
- It can optionally import configuration from a Config Server at `localhost:8888`

> The Config Server and Discovery Server are external dependencies referenced by config and are **not** implemented in this repository.

### Typical request/data flow (as implemented)

1. Client calls API Gateway (in the separate API Gateway repository).
2. Gateway forwards request to this service and includes user context headers:
   - `X-User-Id`
   - `X-User-Email`
   - `X-USER-ROLE`
3. `GatewayAuthenticationFilter` reads those headers and creates Spring Security authentication for this request.
4. Controller executes; some endpoints are guarded by `@PreAuthorize`, while additional role/ownership checks happen in service methods.
5. Service layer reads/writes MySQL via Spring Data JPA repositories.
6. File operations go through `FileStorageService` (`MinioFileStorageService` implementation).
7. Some reads/writes are cached and idempotent operations can be replayed from Redis.
8. For user profile enrichment during paper creation, the service calls `AUTH-SERVICE` via OpenFeign (`GET /users/internal/{id}`).

---

## 3) Current implementation status and scope

### Implemented in this repository

- Spring Boot application with web, validation, security, JPA, caching, Feign, discovery, and actuator wiring
- Research paper + category APIs
- Stateless security with gateway-header-based principal construction
- MinIO-backed file storage implementation
- Redis-backed idempotency records and cache usage
- MapStruct mapping for research-paper DTO transformations
- Global exception handling and API response envelope
- Category seeding on startup (`CommandLineRunner`)

### Present as dependencies but not proven as active runtime feature

- AMQP/RabbitMQ dependency exists, but publisher implementation currently logs only (no broker publish code in this repository)
- Rate-limit filter/service code exists but is commented out and not registered

### Not present yet (future work)

- Docker files
- Docker Compose files
- Kubernetes manifests
- Helm charts
- Container deployment instructions

---

## 4) Technology stack and build details (from `RESEARCH-SERVICE/pom.xml`)

- **Java:** 21
- **Spring Boot parent:** `4.1.0` (as declared in POM)
- **Spring Cloud BOM:** `2025.1.2`
- **Core starters/dependencies declared:**
  - `spring-boot-starter-webmvc`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-security`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-cache`
  - `spring-boot-starter-data-redis`
  - `spring-boot-starter-openfeign`
  - `spring-cloud-starter-netflix-eureka-client`
  - `spring-cloud-starter-config`
  - `spring-cloud-starter-loadbalancer`
  - `spring-boot-starter-actuator`
  - `spring-boot-starter-amqp`
  - `io.minio:minio`
  - `mysql-connector-j` (runtime)
  - `org.mapstruct:mapstruct`
  - `org.projectlombok:lombok`
- **Compiler/build plugins:**
  - `maven-compiler-plugin` (Java 21)
  - annotation processing setup for Lombok + MapStruct
  - `spring-boot-maven-plugin`
- **Maven Wrapper:** present in nested module (`RESEARCH-SERVICE/mvnw`, `RESEARCH-SERVICE/mvnw.cmd`, `.mvn/`)

---

## 5) Repository layout

```text
final-year-project-research-service/
├── README.md
└── RESEARCH-SERVICE/
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/example/RESEARCH_SERVICE/
        │   │   ├── config/
        │   │   ├── controller/
        │   │   ├── dto/
        │   │   ├── entity/
        │   │   ├── enums/
        │   │   ├── exception/
        │   │   ├── filter/
        │   │   ├── mapper/
        │   │   ├── payload/
        │   │   ├── publisher/
        │   │   ├── repository/
        │   │   ├── security/
        │   │   ├── service/
        │   │   └── utils/
        │   └── resources/application.properties
        └── test/java/com/example/RESEARCH_SERVICE/ResearchServiceApplicationTests.java
```

### Important classes/packages

- Boot app: `ResearchServiceApplication`
- Controllers:
  - `ResearchPaperController`
  - `ResearchCategoryController`
- Core services:
  - `ResearchPaperService`
  - `ResearchCategoryService`
  - `ResearchFileServiceImpl`
  - `CurrentUserService`
- Security:
  - `SecurityConfig`
  - `GatewayAuthenticationFilter`
  - `CustomAuthenticationEntryPoint`
  - `CustomAccessDeniedHandler`
- Persistence:
  - `ResearchPaper`, `ResearchCategory`
  - `ResearchPaperRepository`, `ResearchCategoryRepository`
- Idempotency:
  - `@Idempotent`, `IdempotencyAspect`, `IdempotencyService`, `IdempotencyRepository`
- Storage:
  - `FileStorageService`, `MinioFileStorageService`, `MinioConfig`, `MinioProperties`
- External integration:
  - `UserServiceClient` (Feign to `AUTH-SERVICE`)

---

## 6) API response conventions

Most JSON endpoints return `ApiResponse<T>`:

- `success` (boolean)
- `message` (string)
- `status` (HTTP code int)
- `data` (payload)
- `errors` (list of strings; often null on success)
- `path` (request URI)
- `traceId` (UUID from `TraceIdUtil.generate()`)
- `timestamp` (`yyyy-MM-dd HH:mm:ss`)

Pagination payload uses `PagedResponse<T>`:

- `content`, `page`, `size`, `totalElements`, `totalPages`, `first`, `last`
- Page index is exposed as **1-based** in `PagedResponse(Page<T>)`, while request handlers manually convert incoming page params.

### Notable implementation detail

`getPaperSummary` returns an `ApiResponse` where `path`, `traceId`, and `timestamp` are explicitly set to `null`.

---

## 7) Security behavior (as implemented)

- Stateless session policy (`SessionCreationPolicy.STATELESS`)
- CSRF disabled
- `/actuator/**` is `permitAll`
- All other routes require authentication
- `GatewayAuthenticationFilter` trusts gateway-provided headers and builds principal/authorities
- Method security enabled (`@EnableMethodSecurity`)
- Method-level annotations used only on selected paper routes:
  - `hasRole('ADMIN')`
  - `hasAnyRole('ADMIN', 'REVIEWER')`
- Additional role/ownership constraints are enforced in service methods
- Custom handlers:
  - `CustomAuthenticationEntryPoint` for unauthenticated access
  - `CustomAccessDeniedHandler` for forbidden access

> This service does **not** perform JWT parsing/verification in the code shown; it relies on trusted gateway-forwarded identity headers.

---

## 8) Controller routes (implemented endpoints)

Base paths:

- Papers: `/research/papers`
- Categories: `/research/categories`

### Route precedence note

Static mappings like `/search`, `/published`, `/my-papers`, `/author/{authorId}` are defined alongside `/{paperId}` routes. Spring MVC resolves static/specific mappings before variable mappings, so these routes are not treated as `paperId`.

### 8.1 ResearchPaperController routes

| Method | Path | Idempotent | Method-level auth annotation | Purpose / behavior |
|---|---|---:|---|---|
| POST | `/research/papers` | Yes (10m) | None | Create paper (`@Valid CreateResearchPaperRequest`). Requires authenticated user with role `RESEARCHER` or `ADMIN` (service check). Fetches author profile from `AUTH-SERVICE`. |
| GET | `/research/papers/{paperId}` | No | None | Get paper by id. Access allowed for owner/admin/assigned reviewer, or published+public paper. |
| GET | `/research/papers` | No | None | Get paged published+public papers (`page,size,sortBy,sortDirection`). |
| GET | `/research/papers/my-papers` | No | None | Get current user’s papers (`page,size,sortBy,sortDirection`). |
| GET | `/research/papers/author/{authorId}` | No | None | Get paged papers by author id. |
| PUT | `/research/papers/{paperId}` | Yes (3m) | None | Update paper (`@Valid UpdateResearchPaperRequest`). Owner-only and only when status is DRAFT/REJECTED. |
| DELETE | `/research/papers/{paperId}` | No | None | Delete paper. Owner-only and only when editable; deletes storage object if present. |
| PATCH | `/research/papers/{paperId}/submit` | Yes (1m) | None | Submit paper. Requires uploaded file, owner, and DRAFT status. |
| PATCH | `/research/papers/{paperId}/assign-reviewer` | Yes (10m) | `hasRole('ADMIN')` | Assign reviewer (`@Valid AssignReviewerRequest`), transitions SUBMITTED -> UNDER_REVIEW. |
| PATCH | `/research/papers/{paperId}/status` | Yes (2m) | `hasAnyRole('ADMIN','REVIEWER')` | Set review outcome (`@Valid ChangeResearchStatusRequest`) to APPROVED or REJECTED; requires assigned reviewer and UNDER_REVIEW status. |
| PATCH | `/research/papers/{paperId}/publish` | Yes (2m) | `hasAnyRole('ADMIN','REVIEWER')` | Publish paper, only from APPROVED status. |
| GET | `/research/papers/search` | No | None | Search papers using `ResearchPaperSearchRequest` fields + paging params. Specification enforces `status=PUBLISHED` in addition to filters. |
| POST | `/research/papers/{paperId}/upload` (multipart) | Yes (5m) | None | Upload PDF file (`file` form field). Owner-only; rejects non-PDF, empty, >50MB, or already-uploaded file. |
| GET | `/research/papers/{paperId}/download` | No | None | Download file as attachment using `FileDownloadResponse` metadata and stream body. |
| DELETE | `/research/papers/{paperId}/file` | Yes (1m) | None | Delete uploaded file metadata + storage object (if exists). Owner-only. |
| GET | `/research/papers/{paperId}/summary` | No | None | Return compact summary (`PaperSummaryResponse`). |
| POST | `/research/papers/internal/{paperId}/status` | No | None | Internal status update (`ResearchStatusUpdateRequest`). Returns `200` with empty body. |
| GET | `/research/papers/{paperId}/file` | No | None | Download file stream via `ResearchFileService` (attachment headers from metadata). |
| GET | `/research/papers/{paperId}/file/metadata` | No | None | Return file metadata (`ResearchFileResponse`). |
| GET | `/research/papers/published` | No | None | Return list of published+public candidate records (`ResearchCandidateResponse`). |

### 8.2 ResearchCategoryController routes

| Method | Path | Idempotent | Method-level auth annotation | Purpose / behavior |
|---|---|---:|---|---|
| POST | `/research/categories` | Yes (2m) | None | Create category (`@Valid CreateCategoryRequest`). Service enforces ADMIN role. |
| GET | `/research/categories/{id}` | No | None | Get category by id. |
| GET | `/research/categories` | No | None | Get paged categories (`page,size,sortBy`). |
| PUT | `/research/categories/{id}` | Yes (2m) | None | Update category (`UpdateCategoryRequest`). Service enforces ADMIN role. |
| DELETE | `/research/categories/{id}` | No | None | Delete category. Service enforces ADMIN role. |
| GET | `/research/categories/search` | No | (Commented out in code) | Search categories by optional `keyword,id,name` + paging/sort. |

### Status code behavior notes from controller code

- `POST /research/papers` returns HTTP **201**.
- `POST /research/categories` currently returns HTTP **200** while payload `status` field is set to **201**.
- `DELETE /research/categories/{id}` returns **204 No Content**.

---

## 9) Research-paper lifecycle and business rules

Lifecycle statuses available in enum:

- `DRAFT`, `SUBMITTED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `PUBLISHED`, `DELETED`, `REVISION_REQUESTED`, `WITHDRAWN`

Implemented transitions in service logic:

- Create paper -> `DRAFT`
- Submit (`/{id}/submit`) requires uploaded file and `DRAFT` -> `SUBMITTED`
- Assign reviewer (`/{id}/assign-reviewer`) requires `SUBMITTED` and no reviewer -> `UNDER_REVIEW`
- Change status (`/{id}/status`) requires `UNDER_REVIEW` and status in `{APPROVED, REJECTED}`
- Publish (`/{id}/publish`) requires `APPROVED` -> `PUBLISHED`

Additional enforced rules:

- Create allowed only for `RESEARCHER` or `ADMIN` roles (service check)
- Update/delete paper requires ownership and editable status (`DRAFT` or `REJECTED`)
- Upload blocked for `APPROVED` or `PUBLISHED`
- Upload restricted to PDF (`application/pdf`) and max 50MB
- Download increments `downloadCount`
- `search` currently hard-filters to published papers via specification

---

## 10) Idempotency behavior

Endpoints annotated with `@Idempotent(ttlMinutes=...)` require `Idempotency-Key` header.

Implementation characteristics:

- Redis key format: `idempotency:user:{userId}:{idempotencyKey}`
- Fingerprint includes userId + HTTP method + endpoint + normalized request args/body
- If key is in PROCESSING state: error (`IdempotencyProcessingException`)
- If fingerprint differs for same key: conflict (`IdempotencyConflictException`)
- If already completed: cached response is returned
- On controller error, record is removed (`fail`) so request can be retried

---

## 11) Persistence, caching, and storage

### Relational persistence (JPA)

Main entities:

- `ResearchPaper` (`research_papers`)
- `ResearchCategory` (`research_categories`)

`ResearchPaper` includes metadata for:

- author details, category relation
- file object metadata (`fileName`, `contentType`, `fileSize`, `storageKey`)
- workflow status and reviewer/timestamps
- counters (`downloadCount`, `viewCount`)
- inherited `createdAt`/`updatedAt` from `BaseEntity`

### Caching

- Caching is enabled (`@EnableCaching`)
- Redis cache serialization configured in `CacheConfig`
- Cache names defined in `CacheNames`
- `@Cacheable` and `@CacheEvict/@Caching` used heavily in paper/category services

### Redis-backed idempotency

- Separate `RedisTemplate<String, IdempotencyRecord>` via `IdempotencyRedisConfig`
- Stores idempotency processing/completed records with TTL

### Object storage

- `FileStorageService` abstraction
- `MinioFileStorageService` implementation uses `MinioClient`
- `MinioProperties` (`minio.endpoint`, `minio.accessKey`, `minio.secretKey`, `minio.bucket`) are required
- Object key pattern for uploads: `research-papers/{paperId}/v{version}.pdf`

---

## 12) External integrations

### AUTH-SERVICE via OpenFeign

- Feign client name: `AUTH-SERVICE`
- Endpoint called: `GET /users/internal/{id}`
- Purpose: fetch user profile data for paper creation

### Feign header propagation

`FeignConfig` forwards gateway user headers from current authenticated principal:

- `X-User-Id`
- `X-User-Email`
- `X-USER-ROLE`

### Eureka / Config Server

- Eureka client is enabled (`@EnableDiscoveryClient`)
- Config import is optional (`spring.config.import=optional:configserver:...`)
- Both servers are external to this repository

---

## 13) Validation and exception handling

### Validation

- `@Valid` is applied on selected request bodies in controllers
- `MethodArgumentNotValidException` handled in `GlobalExceptionHandler`
- Validation errors return `ApiResponse` with `message="Validation failed"` and `data` as field-error map

### Exception handlers

`GlobalExceptionHandler` maps domain/security/idempotency exceptions to structured `ApiResponse` errors with status, path, traceId, timestamp (except validation handler omits some metadata fields).

Security-specific handlers:

- `CustomAuthenticationEntryPoint` handles unauthenticated requests
- `CustomAccessDeniedHandler` handles access denied

---

## 14) Local prerequisites and run/build commands

Because this repository includes only the research service module, you run commands from:

- `/home/runner/work/final-year-project-research-service/final-year-project-research-service/RESEARCH-SERVICE`

### Prerequisites

- Java 21
- Access to required external services/config for full functionality:
  - MySQL-compatible datasource settings for JPA
  - Redis instance for cache/idempotency
  - MinIO endpoint/credentials/bucket
  - Eureka server (if discovery is used)
  - Optional Spring Cloud Config Server
  - API Gateway/Auth-Service ecosystem for end-to-end authenticated flow

### Maven Wrapper commands

#### Unix-like (Linux/macOS)

```bash
cd /home/runner/work/final-year-project-research-service/final-year-project-research-service/RESEARCH-SERVICE
./mvnw clean test
./mvnw clean package
./mvnw spring-boot:run
```

#### Windows (PowerShell/CMD)

```powershell
cd \home\runner\work\final-year-project-research-service\final-year-project-research-service\RESEARCH-SERVICE
.\mvnw.cmd clean test
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

---

## 15) Testing status (current repository)

Current test source contains only:

- `ResearchServiceApplicationTests` with a single `contextLoads()` test

So, as committed, the automated test suite is minimal and primarily verifies Spring context startup.

---

## 16) Known limitations / current scope

- No containerization assets yet (Docker/Compose/Kubernetes/Helm)
- No JWT verification logic in this service; it trusts gateway-injected identity headers
- Rate limiting implementation exists in codebase but is commented out/inactive
- AMQP dependency is declared, but concrete broker publishing flow is not implemented here
- Some response-status behavior is inconsistent (e.g., category create HTTP status vs payload status)
- Most routes rely on authenticated requests and gateway header propagation for user context

---

## 17) API Gateway reference

For routing and edge security context in the wider system, see:

- **API Gateway repository:** https://github.com/Gideon0123/final-year-project-api-gateway

