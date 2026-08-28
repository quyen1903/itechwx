# itechwx

`itechwx` is an ecommerce backend under active development. It is implemented
as a Java and Spring Boot modular monolith organized around bounded contexts,
with Hexagonal Architecture and Domain-Driven Design (DDD) as its target
architecture.

This README describes the repository as it exists today. Capabilities mentioned
in architecture documents but not present in the source tree are not presented
as implemented features.

## Current status

| Area | Status |
| --- | --- |
| Identity | Provides a shop-account registration route, password hashing, device-session creation, and access/refresh token issuance |
| Security | Provides an explicit public registration route, stateless security, and client-address rate limiting |
| Shop | Contains domain, application, persistence, and unit-test code; it has no HTTP controller and is not called by the Identity registration workflow |
| Login, refresh, and logout | Not implemented as complete HTTP workflows |
| JWT request authentication | No complete bearer-token verification or resource-server flow yet |
| Catalog, inventory, cart, checkout, order, and payment | Architecture direction only; no implementation exists in the current source tree |

The `shop` bounded context is pre-existing repository code. The current
`POST /api/v1/identity/register/shops` workflow creates Identity data only and
does not create a `Shop` aggregate.

## Technology stack

| Capability | Current technology |
| --- | --- |
| Language | Java 25 |
| Framework | Spring Boot 4.1.0 |
| HTTP | Spring Web MVC |
| Security | Spring Security |
| Validation | Jakarta Validation |
| Persistence | Spring Data JPA and Hibernate |
| Local database | PostgreSQL |
| Tokens | Nimbus JOSE through `spring-security-oauth2-jose`; RS256-signed JWTs |
| Passwords | Spring Security `DelegatingPasswordEncoder` |
| Build | Gradle Wrapper with Kotlin DSL |
| Tests | JUnit Platform, AssertJ, Spring test starters, and H2 at test runtime |
| Migrations | Flyway is declared as a dependency but disabled in the current local configuration |

`SECURITY.md`, `LIBRARY.md`, and `CODING_STANDARDS.md` still describe Oracle
Database 23ai as the target database. The executable build and runtime
configuration currently use PostgreSQL. The long-term database decision must
be reconciled before production migrations are enabled.

## Architecture

Target dependency direction:

```text
adapter/in ───────> application ───────> domain
                         │
                         └─────────────> port/out
                                               ▲
                                               │
                                         adapter/out
```

Typical request flow:

```text
HTTP request
  -> request DTO and Jakarta Validation
  -> controller maps the DTO to an application command
  -> input port / use case
  -> domain objects and business rules
  -> output ports
  -> persistence or security adapters
  -> PostgreSQL or security infrastructure
  -> application result
  -> HTTP response DTO
```

Layer responsibilities:

- `adapter/in` accepts requests, validates transport data, and maps request and
  response contracts.
- `application` orchestrates use cases, transactions, and output ports.
- `domain` models accounts, authentication, sessions, tokens, and invariants.
- `adapter/out` implements persistence, password hashing, token signing, and
  other technical integrations.
- `infrastructure` creates and wires Spring beans.

Main source layout:

```text
src/main/java/com/microsoft/itechwx/
├── identity/
│   ├── adapter/in/web
│   ├── adapter/out/persistence
│   ├── adapter/out/security
│   ├── application/contract
│   ├── application/port
│   ├── application/service
│   ├── domain
│   └── infrastructure
├── security/
└── shop/
    ├── adapter/out/persistence
    ├── application
    └── domain
```

The current Identity classes still contain JPA annotations directly in the
`domain` package. This reflects the present implementation, but it does not yet
fully satisfy the domain/persistence separation required by
`CODING_STANDARDS.md`.

## Identity registration workflow

Current endpoint:

```http
POST /api/v1/identity/register/shops
Content-Type: application/json
```

Execution flow:

```text
RegistrationRateLimitFilter
  -> IdentityController
  -> RegisterShopRequest validation
  -> RegisterShopCommand
  -> RegisterShopUseCase / ShopRegister
  -> normalize email and check for duplicates
  -> PasswordHashPort
  -> create Account
       ├── AccountAuthentication
       │    └── DeviceSession
       │         └── RefreshToken hash
       └── AccountProfile
  -> TokenIssuerPort issues a token pair
  -> AccountPort persists the object graph in one transaction
  -> 201 Created with Cache-Control: no-store
```

Current token behavior:

- The access token is an RS256 JWT with a 15-minute lifetime.
- Current claims are `sub = accountId`, `sid`, `email`, `type = ACCESS`, `iat`,
  and `exp`.
- The refresh token contains 32 random bytes encoded with URL-safe Base64 and
  has a 30-day lifetime.
- Only the SHA-256 hash of the refresh token is persisted; the raw token is
  returned in the response.
- The RSA signing key is currently generated in memory when the application
  starts.

Example request:

```bash
curl -i \
  -X POST http://localhost:8081/api/v1/identity/register/shops \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Shop Owner",
    "phone": null,
    "address": null,
    "timezone": "UTC",
    "language": "en",
    "email": "owner@example.invalid",
    "password": "replace-with-a-local-test-password",
    "username": "shop_owner",
    "businessName": "Example Store",
    "businessType": "Retail",
    "taxId": null,
    "currency": "USD"
  }'
```

Current successful response:

```json
{
  "accountId": "<uuid>",
  "accessToken": "<access-token>",
  "refreshToken": "<refresh-token>"
}
```

Business-related request fields are not currently forwarded to the `shop`
bounded context. `ShopRegister` directly uses only `name`, `email`, `password`,
and `username`. Device ID and device name are also generated or hard-coded in
the use case. These areas must be completed before this endpoint represents a
complete shop-registration workflow.

## Current security workflow

- Only `POST /api/v1/identity/register/shops` is explicitly public.
- Every other request requires authentication according to the current
  `SecurityFilterChain`.
- Spring Security uses a stateless session policy.
- Registration is rate-limited by remote client address.
- The local default is five registration attempts per minute for each client
  key.
- Exceeding the limit returns `429 Too Many Requests` with a `Retry-After`
  header.
- The rate limiter and signing keys are both in memory and are not suitable for
  multi-instance production deployment in their current form.

JWT issuance exists, but JWT verification, refresh-token rotation,
logout/session revocation, issuer and audience validation, and role or
permission claims are not yet complete.

## Current persistence workflow

The current local configuration uses:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create
  flyway:
    enabled: false
```

Hibernate therefore recreates the schema from the JPA mappings when the
application starts. This mode is intended for local development only and may
destroy local data.

Once the model is stable, the intended database workflow is:

```text
change the model
  -> add a new Flyway migration
  -> review constraints, indexes, and backfills
  -> apply the migration
  -> run Hibernate with ddl-auto=validate
```

Do not modify a migration after it has been shared. Add `V3`, `V4`, and later
versions for subsequent schema changes.

## Running locally

Requirements:

- JDK 25.
- A local PostgreSQL instance and a database for the project.
- The Gradle Wrapper included in the repository; a global Gradle installation
  is not required.

Datasource configuration can be overridden through environment variables:

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/itechwx'
export SPRING_DATASOURCE_USERNAME='local_user'
export SPRING_DATASOURCE_PASSWORD='replace-me-local-only'
```

Start the application:

```bash
./gradlew bootRun
```

The default address is:

```text
http://localhost:8081
```

When running from VS Code after changing a dependency:

1. Run `Java: Clean Java Language Server Workspace`.
2. Select `Restart and delete`.
3. Wait for the Gradle project import to finish before launching the
   application.

`./gradlew bootRun` is the canonical local launch command because it always
uses the Gradle runtime classpath, including the PostgreSQL JDBC driver.

## Build and test

```bash
# Compile production source
./gradlew compileJava

# Run tests
./gradlew test

# Run the complete build
./gradlew clean build
```

The current test suite focuses on:

- Password-hashing configuration.
- Registration rate-limiter and HTTP-filter behavior.
- Shop aggregate creation through `CreateShopService`.

There is not yet complete test coverage for the registration HTTP contract,
persistence cascade, token claims, refresh-token lifecycle, or Identity failure
paths.

## Feature-development workflow

Recommended order for a vertical slice:

1. Identify the bounded context, actor, use case, and consistency boundary.
2. Define the application command or query, result, and input port.
3. Model aggregate invariants and state transitions in the domain.
4. Define small output ports for persistence, security, and integrations.
5. Implement the application service and transaction boundary.
6. Implement outbound adapters.
7. Add request and response DTOs, validation, and the inbound controller.
8. Add a database migration once Hibernate is no longer creating the local
   schema temporarily.
9. Add appropriate unit, integration, contract, and negative security tests.
10. Run tests and the build, then update Graphify when its CLI is available.

Read project documentation in this order:

1. `AGENTS.md`
2. `SECURITY.md`
3. `LIBRARY.md`
4. `CODING_STANDARDS.md`
5. Relevant source code and tests

## Known gaps and next decisions

- Separate shop-account registration from creation of the Shop business
  aggregate.
- Decide whether to retain or remove the `shop` bounded context; it is not
  connected to the current Identity route.
- Never treat `accountId` and `shopId` as interchangeable JWT or authorization
  scopes.
- Replace in-memory signing keys with durable, configurable key management and
  rotation.
- Add JWT verification and authenticated-principal mapping.
- Add refresh rotation, revocation, logout, and token-reuse detection.
- Remove hard-coded device metadata from the application service.
- Invoke the existing password-policy validation from the registration use
  case; the helper currently exists but is unused.
- Standardize typed exceptions and HTTP error responses.
- Handle database uniqueness races in addition to the advisory duplicate
  lookup.
- Reconcile the current PostgreSQL runtime with the Oracle 23ai target described
  in project standards.
- Re-enable Flyway and use `ddl-auto=validate` before any environment needs
  durable data.
- Separate JPA entities from domain models to satisfy the intended dependency
  direction.

## Related documentation

- [Agent workflow](AGENTS.md)
- [Security standard](SECURITY.md)
- [Library and dependency standard](LIBRARY.md)
- [Coding standard](CODING_STANDARDS.md)
- [Spring-generated help](HELP.md)
