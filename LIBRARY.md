# Library And Dependency Standard

> Entry order: `AGENTS.md` -> `SECURITY.md` -> this file ->
> `CODING_STANDARDS.md` -> relevant requirements -> `build.gradle.kts` ->
> relevant source code.
>
> `build.gradle.kts` is the executable dependency manifest. This document is
> the approval, placement, and usage policy. A mismatch must be corrected in
> the same change that discovers or creates it.

---

## 1. Scope

This standard governs runtime libraries, build plugins, test libraries,
database drivers, code-generation tools, and local/CI development tools used by
the ecommerce platform.

Its goals are to:

- Keep the Java/Spring stack small, supportable, and secure.
- Preserve Hexagonal Architecture and DDD dependency direction.
- Avoid duplicate libraries for the same capability.
- Make dependency ownership, data flow, upgrade risk, and failure mode clear.
- Keep versions reproducible through Gradle and Spring dependency management.

This document does not approve a hosted service, data processor, or production
integration merely because its SDK is listed. External services require the
appropriate product, privacy, security, and operational review.

---

## 2. Current Approved Baseline

The current baseline is derived from `build.gradle.kts`.

| Capability | Approved component | Scope and owner |
| --- | --- | --- |
| Language | Java 25 toolchain | All backend modules |
| Build | Gradle Wrapper with Kotlin DSL | Repository build and CI |
| Application framework | Spring Boot `4.1.0` plugin | Bootstrap, configuration, dependency platform |
| Dependency management | `io.spring.dependency-management` `1.1.7` | Managed dependency versions |
| HTTP server | `spring-boot-starter-webmvc` | Inbound web adapters only |
| Outbound HTTP | `spring-boot-starter-restclient` | Outbound integration adapters only |
| Persistence | `spring-boot-starter-data-jpa` | Outbound persistence adapters only |
| Security | `spring-boot-starter-security` | Security configuration and security adapters |
| JWT | `spring-security-oauth2-jose` and `spring-security-oauth2-resource-server` | RS256 issuance and bearer-token verification in Identity/security adapters |
| Validation | `spring-boot-starter-validation` | Inbound DTO/configuration validation; not domain modeling |
| Migration | `spring-boot-starter-flyway` | Flyway integration; disabled for the current local PostgreSQL schema managed by Hibernate |
| Database | PostgreSQL JDBC | Current local runtime persistence infrastructure |
| Boilerplate generation | Lombok, compile/annotation-processor only | Adapter/configuration boilerplate; restricted in domain |
| JPA testing | `spring-boot-starter-data-jpa-test` | Persistence adapter/integration tests |
| HTTP client testing | `spring-boot-starter-restclient-test` | Outbound HTTP adapter tests |
| Security testing | `spring-boot-starter-security-test` | Authentication/authorization tests |
| Web testing | `spring-boot-starter-webmvc-test` | Inbound HTTP adapter/contract tests |
| Test runtime | `junit-platform-launcher` | JUnit Platform test execution |
| Transaction test database | H2, test runtime only | Fast application-context tests for JPA transaction commit/rollback; Oracle/Flyway behavior still requires Oracle verification |
| Knowledge graph | Graphify CLI | Local/CI architecture navigation; not an application dependency |

Spring-managed dependency versions should normally omit an explicit version.
Plugin and Java toolchain versions remain explicit in the build.

### Baseline constraints

- Repositories are limited to approved sources; currently `mavenCentral()`.
- Do not use dynamic versions, version ranges, `latest.release`, or snapshots in
  the main build.
- Do not add another web framework, ORM, migration tool, validation framework,
  JSON stack, logging facade, password library, or test framework without an
  explicit architecture decision and an update to this file.
- Use the Gradle Wrapper committed to the repository. Do not require a
  developer's global Gradle installation.

---

## 3. Mandatory Dependency Workflow

Before adding or upgrading a dependency:

1. Identify the bounded context and adapter that owns the capability.
2. Confirm that Java, Spring Boot, or an existing dependency cannot safely
   provide it.
3. Evaluate maintenance activity, release cadence, license, known
   vulnerabilities, transitive dependencies, Java/Spring compatibility, and
   expected support lifetime.
4. Document what data the library receives, where it sends data, whether it has
   telemetry, how it handles retries/timeouts, and how it fails.
5. Prefer a small port owned by the application and keep the library type inside
   one outbound adapter.
6. Add the narrowest Gradle configuration: `implementation`, `runtimeOnly`,
   `compileOnly`, `annotationProcessor`, or `testImplementation`.
7. Update this file when the approved baseline or policy changes.
8. Add tests for the adapter and failure mode.
9. Run dependency resolution, tests, and the application build.
10. Review generated lockfiles, reports, and transitive changes where present.

Dependency changes affecting authentication, authorization, cryptography,
payments, uploads, serialization, database access, migrations, messaging, or
network calls require security review under `SECURITY.md`.

---

## 4. Hexagonal Placement Rules

Libraries must remain at the edge of the architecture.

```text
domain                      plain Java only
application                 domain/application types + ports
adapter/in/web              Spring MVC, Jakarta Validation, JSON mapping
adapter/out/persistence     Spring Data JPA, Hibernate, Oracle mappings
adapter/out/security        Spring Security, PasswordEncoder, JWT implementation
adapter/out/integration     RestClient and provider SDKs
adapter/out/messaging       Kafka/queue/outbox implementation
configuration               Spring bean and module wiring
```

Rules:

- Domain classes must not import Spring, JPA/Hibernate, Jackson, Jakarta
  Validation, a database driver, Redis/Kafka client, or provider SDK.
- Application ports must not expose framework entities, HTTP types, ORM types,
  vendor DTOs, or provider exceptions.
- Adapters translate vendor/framework failures into typed application failures.
- JPA entities live in persistence adapters and are explicitly mapped to/from
  domain aggregates or query projections.
- Web DTO annotations validate transport shape. Domain factories and value
  objects independently protect business invariants.
- Spring Security establishes authenticated actor context, but application use
  cases still enforce principal type, permission, ownership, `userId`, and
  `shopId` scope.
- A provider SDK must not become the domain model. Wrap only the operations the
  use case actually needs.

### Lombok

Lombok is allowed for focused boilerplate reduction in adapters,
configuration, persistence entities, and immutable application contracts.

Do not use Lombok to:

- Generate public setters for aggregates.
- Hide invalid default constructors or partially initialized domain objects.
- Generate equality for JPA entities without understanding proxy and identity
  behavior.
- Put `@Data` on domain aggregates or security-sensitive objects.
- Expose passwords, hashes, tokens, secrets, or private fields through generated
  `toString()` methods.

Prefer explicit domain constructors/factories and named behavior methods.

---

## 5. Approved Usage By Capability

### Persistence and migrations

- Use Spring Data JPA through outbound persistence adapters.
- Do not inject Spring Data repositories into controllers or domain services.
- Do not expose JPA entities through input/output ports or API responses.
- Use Flyway for every shared schema change.
- Never edit a migration after it has been shared; add a new migration.
- Add indexes and database constraints required by domain invariants and query
  patterns in the same logical migration.
- Oracle-specific SQL is allowed in Flyway migrations and intentionally
  named persistence queries, not in the domain.
- Historical migrations for unsupported database engines belong outside
  `db/migration/` so Flyway cannot discover two scripts with the same version.

### Authentication and cryptography

- Use Spring Security's maintained `PasswordEncoder` abstraction through an
  application-owned password hashing port.
- Prefer `PasswordEncoderFactories.createDelegatingPasswordEncoder()` so stored
  hashes carry an algorithm identifier and can migrate over time.
- Do not implement custom password hashing, token signing, encryption, secure
  random generation, or key parsing.
- Use Spring Security OAuth2 JOSE/resource-server support and Nimbus only
  through the Identity security adapters.
- Each successful registration or login creates a new RSA key pair. Persist
  only the public key and `kid`; never persist the private key.
- Resolve verification keys from the database by `kid`. There is no
  scheduled active-key rotation in the current early-stage design.
- Store only refresh-token hashes/non-reversible verifiers; raw bearer tokens
  are never persistence values.

### HTTP clients and provider SDKs

- Use Spring `RestClient` in outbound adapters for ordinary synchronous HTTP
  integration.
- Configure explicit connect/read timeouts, bounded retries, safe logging, and
  error translation.
- Retry only idempotent operations or operations protected by an idempotency
  key.
- Do not allow provider request/response classes to cross an output port.
- Add a vendor SDK only when it materially improves correctness or required
  protocol support over the existing HTTP client.

### Validation

- Use Jakarta Validation for web DTOs and typed configuration.
- Do not treat annotations as the only protection for domain invariants.
- Queue events, scheduled-job payloads, database mappings, and external provider
  responses also require validation at their boundary.

### Testing

- Use JUnit Platform and the smallest relevant Spring test slice.
- Prefer plain unit tests for domain and application logic; they should not
  start a Spring context.
- Use JPA tests for mappings, constraints, locking, and repository adapter
  behavior.
- Use WebMVC and Security tests for validation, authentication, authorization,
  error mapping, and API contracts.
- Use RestClient tests for outbound request mapping, timeouts, safe retries, and
  failure translation.
- Add Testcontainers or another infrastructure dependency only after recording
  the rationale, CI impact, image provenance, and version policy here.

---

## 6. Version And Upgrade Policy

- Prefer the Spring Boot dependency platform for compatible Spring ecosystem
  versions.
- Pin Gradle plugins, container images, GitHub Actions, and tools not managed by
  the Spring platform.
- Upgrade one logical platform group at a time and review release/migration
  notes before merging.
- Major Java, Spring Boot, Hibernate, Flyway, Oracle JDBC driver, security, or
  serialization upgrades require integration tests and a rollback/roll-forward
  plan.
- Do not suppress dependency vulnerabilities without a documented affected
  path analysis, compensating control, owner, and expiration date.
- Remove unused direct dependencies. Do not rely accidentally on a transitive
  dependency; declare it directly when application source intentionally uses
  it.

When an emergency security upgrade must precede full migration work, document
the temporary compatibility risk and create a bounded follow-up action.

---

## 7. Graphify

Graphify is an approved development tool and is not a Gradle/runtime
dependency. Its generated project graph lives in `graphify-out/`.

Standard commands:

```text
graphify update .
graphify query "<codebase question>"
graphify explain "<concept>"
graphify path "<A>" "<B>"
graphify affected "<concept>"
```

Rules:

- Use `graphify update .` after code or module-structure changes. This AST-only
  update does not require an API key.
- Query or explain the graph before broad raw-source searches when
  `graphify-out/graph.json` exists.
- Do not manually edit generated graph, report, visualization, wiki, or memory
  files.
- Graph output may be committed when the team wants repository-local knowledge
  continuity. Dirty generated output after a legitimate update is expected.
- Installing Git hooks or global Graphify skills is a developer-machine choice;
  do not make it a hidden build requirement.
- Graphify output is navigational evidence, not a substitute for reading the
  source, migrations, tests, and security requirements before a change.

---

## 8. Examples

Approved Gradle placement:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
}
```

Approved adapter isolation:

```java
public interface PasswordHashPort {
    PasswordHash hash(RawPassword password);
    boolean matches(RawPassword password, PasswordHash hash);
}
```

The Spring `PasswordEncoder` remains inside the outbound security adapter; the
domain/application contract does not expose it.

Dependency change record:

```text
Dependency:
Capability and bounded-context owner:
Why existing stack is insufficient:
Runtime/build/test scope:
Data received or transmitted:
License and maintenance review:
Transitive/security review:
Timeout/retry/failure behavior:
Tests added:
Rollback/removal plan:
```

---

## 9. Anti-Patterns

| Do not | Do |
| --- | --- |
| Add a library for a trivial utility | Use the Java/Spring capability already present |
| Use framework/vendor types in domain code | Define a domain/application type and map in an adapter |
| Inject `JpaRepository` into a controller | Inject an input port/use case |
| Return a JPA entity from an application port | Return an aggregate, value object, result, or projection |
| Add a second ORM or migration tool casually | Use JPA and Flyway or record an architecture decision |
| Declare dynamic or snapshot versions | Pin or inherit a managed stable version |
| Add an SDK without timeout/data review | Document data flow, failure mode, and adapter ownership |
| Use Lombok `@Data` on aggregates or secrets | Write explicit behavior and safe representations |
| Store raw refresh tokens | Store hashes/verifiers and implement rotation/revocation |
| Hand-edit `graphify-out` | Regenerate it with Graphify |

---

## 10. Dependency Checklist

- [ ] The capability and bounded-context owner are clear.
- [ ] The Java/Spring/current-stack alternative was evaluated.
- [ ] The library is confined to the correct adapter or build/test scope.
- [ ] No framework/vendor type leaks into domain or application ports.
- [ ] Version management is deterministic.
- [ ] License, maintenance, vulnerabilities, and transitive dependencies were
      reviewed.
- [ ] Data collection, telemetry, destinations, secrets, and privacy impact are
      understood.
- [ ] Timeout, retry, idempotency, and failure behavior are defined.
- [ ] Tests cover integration and failure paths.
- [ ] `build.gradle.kts` and this file agree.
- [ ] Security-sensitive changes were reviewed under `SECURITY.md`.
- [ ] Graphify was updated when code/module structure changed.

---

## Related

- `AGENTS.md`
- `SECURITY.md`
- `CODING_STANDARDS.md`
- `build.gradle.kts`
- `settings.gradle.kts`
