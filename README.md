# Research Service

The **Research Service** is a Spring Boot microservice for the **Design and Implementation of a Web-Based Academic Digital Repository and Collaboration System**. It is responsible for managing academic research papers, research categories, paper metadata, research-paper lifecycle operations, reviewer assignment, paper status changes, publication, file uploads, file downloads, and related research-file operations.

This repository contains the Research Service only. It is designed to operate as one service within a wider microservice system and is intended to be accessed directly during development or through the project API Gateway in the complete application.

> **Current scope:** This repository does not currently include Docker, Docker Compose, Kubernetes manifests, Helm charts, or container deployment instructions. Containerization and orchestration are future work and are not part of the current implementation.

## Related repositories and services

- **Research Service:** [Gideon0123/final-year-project-research-service](https://github.com/Gideon0123/final-year-project-research-service)
- **API Gateway:** [Gideon0123/final-year-project-api-gateway](https://github.com/Gideon0123/final-year-project-api-gateway)

The API Gateway exposes the wider system through shared routes and forwards Research Service requests to the Eureka service ID `RESEARCH-SERVICE`.

The committed `application.properties` also shows that this service is intended to work with:

- A Spring Cloud Config Server at `http://localhost:8888`.
- A Eureka discovery server at `http://localhost:8761/eureka`.
- Other infrastructure configured through external configuration, including the database, Redis, MinIO, and messaging settings.

## What the service provides

The Research Service currently implements the following areas:

### Research paper management

- Create research papers.
- Retrieve a research paper by ID.
- Retrieve paginated research-paper summaries.
- Retrieve the authenticated user's research papers.
- Retrieve papers belonging to a specific author.
- Update research-paper information.
- Delete a research paper.
- Submit a research paper.
- Assign a reviewer to a research paper.
- Change the status of a research paper.
- Publish a research paper.
- Search research papers using search criteria and pagination.
- Retrieve a paper summary.
- Receive internal status updates from another service.

### Research-file management

- Upload a file for a research paper using multipart form data.
- Download an uploaded paper file.
- Delete the uploaded file associated with a research paper.
- Download a research file using the dedicated file endpoint.
- Retrieve research-file metadata.
- Retrieve published research candidates.
- Store research files through the MinIO integration.

The code contains both paper download methods and dedicated research-file methods. These methods expose file content as streaming HTTP responses and use stored metadata such as file name, content type, and file size.

### Research-category management

- Create research categories.
- Retrieve a category by ID.
- Retrieve paginated categories.
- Update a category.
- Delete a category.
- Search categories using optional keyword, ID, and name criteria.
- Seed predefined academic categories when the application starts.

The `CategorySeeder` checks whether each category already exists before inserting it, so existing categories are not inserted again during startup.

The predefined category list includes areas such as:

- Computer Science
- Cyber Security
- Artificial Intelligence
- Software Engineering
- Data Science
- Computer Engineering
- Information Technology
- Electrical Engineering
- Mechanical Engineering
- Medicine
- Agriculture
- Business Administration
- Accounting
- Economics
- Law
- Education
- Biochemistry
- Microbiology
- Physics
- Chemistry
- Mathematics
- Pharmacy
- Psychology

## Architecture

The service follows a layered Spring Boot architecture:

1. **Controllers** receive HTTP requests, validate input, construct responses, and delegate work to services.
2. **Services** implement research-paper, category, file-storage, idempotency, rate-limiting, user-lookup, and supporting business operations.
3. **Repositories** provide persistence access through Spring Data JPA and include repositories for papers, categories, and idempotency records.
4. **Entities** represent persisted domain objects such as research papers, categories, base records, current-user data, idempotency records, request fingerprints, and idempotency results.
5. **DTOs and payloads** define request and response structures used by the HTTP API.
6. **Mappers** support conversion between persistence entities and API-facing DTOs.
7. **Specifications** support dynamic repository queries used by search operations.
8. **Security and filters** authenticate requests using information forwarded by the API Gateway and apply stateless authorization rules.
9. **Infrastructure configuration** connects the service to Eureka, Spring Cloud Config, Redis, MinIO, RabbitMQ-related components, and the relational database.

The application starts from `ResearchServiceApplication`. It enables Spring Boot, Eureka discovery, Spring caching, and OpenFeign clients:

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableFeignClients
public class ResearchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResearchServiceApplication.class, args);
    }
}
```

## Technology stack

- **Java 21**
- **Spring Boot 4.1.0** as declared in the Research Service Maven project
- **Spring Cloud 2025.1.2** dependency management
- Spring MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Cloud Netflix Eureka Client
- Spring Cloud Config Client
- Spring Cloud OpenFeign
- Spring Cloud LoadBalancer
- Spring Cache
- Spring Data Redis
- Spring AMQP
- MinIO Java client
- MySQL Connector/J
- MapStruct
- Lombok
- Maven Wrapper
- JUnit and Spring Boot test support

The exact dependency declarations are available in [`RESEARCH-SERVICE/pom.xml`](https://github.com/Gideon0123/final-year-project-research-service/blob/main/RESEARCH-SERVICE/pom.xml).

## Repository structure

The repository currently contains the service under the `RESEARCH-SERVICE` directory.

```text
final-year-project-research-service/
├── README.md
└── RESEARCH-SERVICE/
    ├── .mvn/                         Maven Wrapper support
    ├── mvnw                          Unix-like Maven Wrapper script
    ├── mvnw.cmd                      Windows Maven Wrapper script
    ├── pom.xml                       Maven project and dependency configuration
    └── src/
        ├── main/
        │   ├── java/com/example/RESEARCH_SERVICE/
        │   │   ├── ResearchServiceApplication.java
        │   │   ├── config/             Application, cache, security, MinIO, Redis, Feign, and seeding configuration
        │   │   ├── controller/         Research paper and category REST controllers
        │   │   ├── dto/                Request and response DTOs
        │   │   ├── entity/              JPA entities and idempotency records
        │   │   ├── enums/               Domain enumerations
        │   │   ├── exception/            Application exception handling
        │   │   ├── filter/               Request authentication filters
        │   │   ├── mapper/               Entity-to-DTO mapping components
        │   │   ├── payload/              Shared response payloads, including pagination
        │   │   ├── publisher/             Messaging publisher components
        │   │   ├── repository/            Spring Data repositories and specifications
        │   │   ├── security/              Authentication and authorization support
        │   │   ├── service/               Business services and integrations
        │   │   └── utils/                 Idempotency, tracing, MinIO properties, and other utilities
        │   └── resources/
        │       └── application.properties
        └── test/
            └── java/com/example/RESEARCH_SERVICE/
                └── ResearchServiceApplicationTests.java
```

## HTTP API

The API is implemented by:

- `ResearchPaperController`, mapped to `/research/papers`.
- `ResearchCategoryController`, mapped to `/research/categories`.

Successful API responses generally use the shared `ApiResponse` structure. The response model can include:

- Whether the operation succeeded.
- A human-readable message.
- The HTTP status code.
- Response data.
- Validation or operation errors where applicable.
- The request path.
- A generated trace ID.
- A timestamp.

Paginated operations use `PagedResponse`, which includes the page content, page size, current page, first/last-page indicators, total elements, and total pages.

### Research-paper endpoints

All endpoints in this section are relative to `/research/papers`.

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/research/papers` | Create a research paper. The request is validated and the operation is protected by idempotency handling. |
| `GET` | `/research/papers/{paperId}` | Retrieve one research paper by ID. |
| `GET` | `/research/papers` | Retrieve paginated research-paper summaries. Supports `page`, `size`, `sortBy`, and `sortDirection`. |
| `GET` | `/research/papers/my-papers` | Retrieve paginated papers belonging to the current user. Supports `page`, `size`, `sortBy`, and `sortDirection`. |
| `GET` | `/research/papers/author/{authorId}` | Retrieve paginated papers belonging to a specified author. Supports `page`, `size`, `sortBy`, and `sortDirection`. |
| `PUT` | `/research/papers/{paperId}` | Update an existing research paper. |
| `DELETE` | `/research/papers/{paperId}` | Delete a research paper. |
| `PATCH` | `/research/papers/{paperId}/submit` | Submit a research paper for the next stage of processing. |
| `PATCH` | `/research/papers/{paperId}/assign-reviewer` | Assign a reviewer to a paper. Requires the `ADMIN` role. |
| `PATCH` | `/research/papers/{paperId}/status` | Change the status of a paper. Requires the `ADMIN` or `REVIEWER` role. |
| `PATCH` | `/research/papers/{paperId}/publish` | Publish a paper. Requires the `ADMIN` or `REVIEWER` role. |
| `GET` | `/research/papers/search` | Search papers using `ResearchPaperSearchRequest` criteria, pagination, and sorting. |
| `POST` | `/research/papers/{paperId}/upload` | Upload a paper file using `multipart/form-data` with a `file` request parameter. |
| `GET` | `/research/papers/{paperId}/download` | Download the paper file as a streaming response. |
| `DELETE` | `/research/papers/{paperId}/file` | Delete the uploaded research document associated with a paper. |
| `GET` | `/research/papers/{paperId}/summary` | Retrieve a summary representation of a paper. |
| `POST` | `/research/papers/internal/{paperId}/status` | Internal endpoint for applying a `ResearchStatusUpdateRequest`. |
| `GET` | `/research/papers/{paperId}/file` | Download the research file using stored file metadata. |
| `GET` | `/research/papers/{paperId}/file/metadata` | Retrieve file name, content type, and file size metadata. |
| `GET` | `/research/papers/published` | Retrieve published research-paper candidates. |

#### Paper-list query parameters

The paginated paper endpoints use the following parameters where applicable:

| Parameter | Default in controller | Description |
|---|---:|---|
| `page` | `0` for general paper lists | Requested page value. The controller adjusts the value before creating the Spring `Pageable`. |
| `size` | `10` | Number of results per page. |
| `sortBy` | `createdAt` for general paper lists | Entity field used for sorting. |
| `sortDirection` | `desc` for general paper lists | Sort direction passed to the service. |

The search endpoint uses `page=1`, `size=10`, and `sortBy=id` as its controller defaults.

### Research-category endpoints

All endpoints in this section are relative to `/research/categories`.

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/research/categories` | Create a research category. |
| `GET` | `/research/categories/{id}` | Retrieve a category by ID. |
| `GET` | `/research/categories` | Retrieve paginated categories. Supports `page`, `size`, and `sortBy`. |
| `PUT` | `/research/categories/{id}` | Update a category. |
| `DELETE` | `/research/categories/{id}` | Delete a category. |
| `GET` | `/research/categories/search` | Search categories by optional `keyword`, `id`, and `name` criteria with pagination and sorting. |

#### Category query parameters

| Parameter | Default | Description |
|---|---:|---|
| `page` | `1` | Requested page value. The controller converts it to the internal zero-based page value. |
| `size` | `5` for the main category list | Number of categories per page. The search endpoint defaults to `10`. |
| `sortBy` | `id` | Entity field used for sorting. |
| `keyword` | Not required | Optional search value for category searching. |
| `id` | Not required | Optional category ID filter. |
| `name` | Not required | Optional category name filter. |

## Authentication and authorization

The Research Service is configured as a stateless Spring Security application. The security configuration:

- Disables CSRF for the service API.
- Uses `SessionCreationPolicy.STATELESS`.
- Permits `/actuator/**`.
- Requires authentication for other requests.
- Registers `GatewayAuthenticationFilter` before Spring Security's username/password authentication filter.
- Enables method-level security through `@EnableMethodSecurity`.
- Uses custom authentication-entry-point and access-denied handlers.

The service is designed to receive authenticated user information from the API Gateway. The gateway validates the JWT and forwards user-related headers to downstream services. The Research Service's gateway authentication filter converts those forwarded values into the authenticated security context used by controller and service authorization checks.

Controller-level role restrictions currently include:

- Reviewer assignment: `ADMIN` only.
- Research-paper status changes: `ADMIN` or `REVIEWER`.
- Research-paper publication: `ADMIN` or `REVIEWER`.

The API Gateway provides the external security boundary for the wider system. Its `/research/**` route forwards requests to `lb://RESEARCH-SERVICE` through Eureka and applies gateway-level rate limiting, circuit breaking, and retries.

## Persistence and integrations

### MySQL and Spring Data JPA

The Maven project includes MySQL Connector/J and Spring Data JPA. The research domain is represented through JPA entities and repositories, including:

- `ResearchPaper`
- `ResearchCategory`
- `BaseEntity`
- `IdempotencyRecord`
- `CurrentUser`
- Request-fingerprint and idempotency-result models

Repositories include:

- `ResearchPaperRepository`
- `ResearchCategoryRepository`
- `IdempotencyRepository`
- Research repository specifications for dynamic search operations

The actual database connection values are not present in the committed `application.properties`; they are expected to be supplied through the configured external configuration mechanism or another runtime configuration source.

### MinIO object storage

The service includes `MinioConfig`, `MinioProperties`, `MinioFileStorageService`, and file-service abstractions for storing and retrieving research documents. The MinIO client is created using an external endpoint, access key, and secret key.

File operations are separated from the main paper metadata operations through file-storage services. This allows paper records and binary document storage to be handled independently while still being connected through the paper ID.

The MinIO endpoint and credentials are not committed to the repository's local properties file and must be supplied through runtime configuration.

### Redis

Redis is used by the service for infrastructure concerns including:

- Spring Cache configuration.
- Idempotency records and request de-duplication support.
- Rate-limit-related service functionality.

`CacheConfig` configures JSON serialization for Redis cache values. `RedisConfig` and `IdempotencyRedisConfig` provide Redis-related configuration components.

### RabbitMQ and publishers

The project includes the Spring AMQP dependency and a publisher package. RabbitMQ connection and exchange/queue settings are not declared in the committed `application.properties`, so they must be provided through the external configuration used by the running environment.

### Eureka service discovery

The application enables Eureka discovery with `@EnableDiscoveryClient` and uses the service name:

```properties
spring.application.name=RESEARCH-SERVICE
```

The committed configuration points to:

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true
```

This allows the service to register with Eureka and be discovered by the API Gateway and other participating services.

### Spring Cloud Config

The service imports configuration from Spring Cloud Config using an optional import:

```properties
spring.config.import=optional:configserver:http://localhost:8888
```

Because the import is optional, the service can start without a reachable Config Server only when all required runtime properties are otherwise available. In a complete local microservice environment, the Config Server is expected to provide environment-specific settings such as database, Redis, MinIO, messaging, and other integration properties.

## Configuration currently committed

The repository currently commits the following local configuration in `RESEARCH-SERVICE/src/main/resources/application.properties`:

```properties
spring.application.name=RESEARCH-SERVICE

logging.level.org.springframework.security=TRACE

spring.config.import=optional:configserver:http://localhost:8888

eureka.client.service-url.defaultZone=http://localhost:8761/eureka

eureka.instance.prefer-ip-address=true
```

Security logging is set to `TRACE`, which can be useful during development but may produce verbose logs. Review this setting before using the service in a production environment.

The committed properties file does not contain database credentials, Redis credentials, MinIO credentials, RabbitMQ settings, JWT secrets, or deployment-specific values. Those values should be supplied through the external configuration system or another secure runtime configuration mechanism.

## Running locally

### Prerequisites

Install or make available:

1. Java 21.
2. A compatible Maven environment, or use the included Maven Wrapper.
3. A MySQL database configured for the service.
4. A Redis instance.
5. A MinIO server for research-file storage.
6. A reachable Eureka Server at `http://localhost:8761/eureka`, unless discovery is replaced or disabled through environment-specific configuration.
7. A reachable Spring Cloud Config Server at `http://localhost:8888` when external configuration is being used.
8. RabbitMQ when the configured messaging features require it.
9. The related services required by Feign clients and the wider application workflow.

### Start the service with Maven Wrapper

The Maven project is located in the `RESEARCH-SERVICE` directory.

#### Windows PowerShell

```powershell
cd RESEARCH-SERVICE
.\mvnw.cmd spring-boot:run
```

#### macOS or Linux

```bash
cd RESEARCH-SERVICE
./mvnw spring-boot:run
```

### Build the project

#### Windows PowerShell

```powershell
cd RESEARCH-SERVICE
.\mvnw.cmd clean package
```

#### macOS or Linux

```bash
cd RESEARCH-SERVICE
./mvnw clean package
```

### Run tests

The repository currently contains a Spring Boot context-load test in:

```text
RESEARCH-SERVICE/src/test/java/com/example/RESEARCH_SERVICE/ResearchServiceApplicationTests.java
```

Run the test suite with:

#### Windows PowerShell

```powershell
cd RESEARCH-SERVICE
.\mvnw.cmd test
```

#### macOS or Linux

```bash
cd RESEARCH-SERVICE
./mvnw test
```

The current test verifies that the Spring application context can load. Full end-to-end behaviour still depends on the required external infrastructure and configuration being available.

## Request processing flow

A typical request through the complete system follows this flow:

1. A client sends a request to the API Gateway.
2. The API Gateway examines the request path and matches `/research/**` routes.
3. The gateway validates the JWT when the endpoint requires authentication.
4. The gateway forwards authenticated user information through request headers.
5. Eureka resolves the logical service ID `RESEARCH-SERVICE` to a service instance.
6. The request reaches the Research Service controller.
7. `GatewayAuthenticationFilter` establishes the authenticated user context.
8. Spring Security and method-level security evaluate access rules.
9. The controller validates request data and delegates to a service class.
10. The service accesses the database, Redis, MinIO, messaging components, or Feign clients as required.
11. The controller returns an `ApiResponse`, `PagedResponse`, or streaming file response.

For file downloads, the response may be returned as an `InputStreamResource` with content type, content length, and a `Content-Disposition` filename header.

## Idempotency and request safety

Several write operations use the custom `@Idempotent` annotation. The annotation is applied to operations such as:

- Creating a research paper.
- Updating a research paper.
- Submitting a paper.
- Assigning a reviewer.
- Changing a paper status.
- Publishing a paper.
- Uploading a paper file.
- Deleting an uploaded file.
- Creating and updating categories.

The annotation includes operation-specific TTL values. Idempotency support is implemented through the idempotency service, Redis configuration, idempotency repository, request-fingerprint models, and idempotency-result models.

The purpose of this mechanism is to prevent repeated processing of duplicate write requests within the configured idempotency window.

## Pagination and searching

The service uses Spring Data pagination through `Pageable`, `PageRequest`, and `Sort`.

Paper and category list endpoints return a `PagedResponse` containing:

- `content`
- `size`
- `page`
- `first`
- `last`
- `totalElements`
- `totalPages`

Research-paper search accepts a `ResearchPaperSearchRequest` and creates a pageable request using the requested page, size, and sort field.

Category search accepts optional filters for:

- `keyword`
- `id`
- `name`

Dynamic query support is organized under the repository specification package.

## Error handling and observability

The project includes custom exception and security handling packages. Successful controller responses include trace IDs for most API operations through `TraceIdUtil.generate()`.

The service also includes:

- Spring Boot Actuator.
- Security trace logging in the committed application properties.
- Custom authentication-entry-point handling.
- Custom access-denied handling.
- Standardized API response structures.
- Request path and timestamp information in response payloads.

The exact error response for a given failure depends on the exception, validation, security, and handler involved.

## API Gateway relationship

The API Gateway repository contains the external route definitions for the Research Service. The active gateway routes include:

| Gateway route | Destination | Behaviour |
|---|---|---|
| `/research/papers/*/upload` with `POST` | `lb://RESEARCH-SERVICE` | Dedicated upload forwarding route. |
| `/research/papers/*/download` with `GET` | `lb://RESEARCH-SERVICE` | Dedicated download forwarding route. |
| `/research/**` | `lb://RESEARCH-SERVICE` | General Research Service route with Redis rate limiting, circuit breaking, and retries for selected HTTP methods and failure statuses. |

The gateway's more specific upload and download routes are declared before the general `/research/**` route. This ensures those paths can be matched by their dedicated method-specific routes first.

The gateway repository is available at:

[https://github.com/Gideon0123/final-year-project-api-gateway](https://github.com/Gideon0123/final-year-project-api-gateway)

## Development notes

- The service is currently a Java and Maven project.
- The service is located in the nested `RESEARCH-SERVICE` directory rather than directly at the repository root.
- The service uses Java 21 in the Maven compiler configuration.
- Configuration is intentionally split between committed local properties and external configuration.
- The service expects to participate in a larger microservice environment.
- Containerization is not implemented yet.
- Kubernetes deployment is not implemented yet.
- No Dockerfile, Docker Compose file, Kubernetes manifest, or Helm chart is included in the current repository.
- The test suite currently contains a context-load test rather than a complete endpoint integration test suite.

## Current limitations and future work

The following items are outside the current repository implementation or are dependent on future project work:

- Docker and Docker Compose support.
- Kubernetes manifests and Helm deployment support.
- Production deployment configuration.
- A complete externally documented environment-variable catalogue.
- A comprehensive endpoint integration-test suite.
- Deployment-specific monitoring and operational dashboards.

These items should not be treated as existing features of the current Research Service.

## License

No license is currently declared in the repository metadata.
