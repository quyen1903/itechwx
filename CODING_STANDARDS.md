# Ecommerce Coding Standards

> Entry: `AGENTS.md` -> `SECURITY.md` -> `LIBRARY.md` -> this file.
> Product source: `requirements/01_product_strategy/PRD.md` if present.
> Architecture source: `requirements/03_technical_specs/TECHSTACK.md` if present.
> Testing source: `requirements/04_testing_qa/TEST-CASES.md` if present.

---

## 1. Operating Principles

### SOLID + DRY + Shop/User Safety

| Principle | Rule |
| --- | --- |
| S | One class/module owns one reason to change. An `OrderController` is not a payment client, inventory calculator, notification sender, and audit logger. |
| O | Add new route handlers, policies, calculators, or adapters instead of growing unrelated switches. |
| L | Repository, service, and adapter interfaces must remain substitutable in tests. |
| I | Keep interfaces small. Do not expose a 20-method "manager" interface when a feature needs 2 methods. |
| D | Domain/application code depends on abstractions, not concrete HTTP clients, Redis clients, payment SDKs, or database drivers. |
| DRY | Extract duplication after the 2nd real use, but only into a module/package that has a clear owner and name. |
| Safety | Every shop-owned operation must prove `shopId` scope. Every user-private operation must prove `userId` scope. Admin bypasses require explicit admin authorization. |

### Non-negotiables

- No unvalidated external input.
- No raw secrets in code, docs, logs, screenshots, fixtures, or examples.
- No user token on shop-only APIs.
- No shop token on user-private APIs unless the business flow explicitly allows it.
- No cross-shop reads, writes, exports, jobs, inventory changes, or order actions without an explicit admin path.
- No client-provided `userId`, `shopId`, role, permission, price, stock, discount, shipping fee, or payment status as source of truth.
- No unsafe inventory read-modify-write logic.
- No silent `catch` blocks. Convert errors into typed domain/application failures or structured API errors.
- No unrelated refactors mixed into a bounded feature change.

### Preferred backend feature flow

```text
External actor
  -> Inbound adapter: HTTP controller / event consumer / scheduler
  -> Boundary DTO validation and mapping
  -> Input port
  -> Application use case: orchestration, authorization, transaction
  -> Domain aggregate / value object / policy / event
  -> Output port
  -> Outbound adapter: JPA / security / messaging / external client
  -> PostgreSQL / Redis / Kafka / external provider
  -> Stable result mapped by the inbound adapter
```

### Preferred frontend feature flow

```text
Route
  -> Page shell
  -> Feature component
  -> Form/table/view model
  -> TanStack Query mutation/query
  -> typed API client
  -> backend API
```

### Preferred ecommerce workflow flow

```text
User action
  -> validate request
  -> resolve authenticated actor
  -> verify ownership/scope
  -> load product/SKU/cart/order state
  -> apply domain policy
  -> run transaction when state changes must be atomic
  -> persist state
  -> emit non-critical events after commit
  -> return stable response DTO
```

---

## 2. Source Of Truth Order

When documents or implementation disagree, use this order:

1. Explicit user instruction from the project owner.
2. Security requirements in `SECURITY.md`.
3. Product requirements in `PRD.md`, detailed requirements, epics, and user stories.
4. Technical architecture in `TECHSTACK.md` and module design docs.
5. Test cases in `TEST-CASES.md`.
6. Existing implementation patterns in the current module.
7. Library guidance in `LIBRARY.md`.
8. Agent or assistant preference.

If a change contradicts a higher-priority source, update the source document in the same change or call it out clearly.

---

## 3. Repository Shape

Use the existing repository shape first. New backend bounded contexts use the
following Java package structure:

```text
src/main/java/com/microsoft/itechwx/<boundedcontext>/
  domain/
    model/                  # Aggregate roots and entities
    valueobject/            # Immutable domain values
    policy/                 # Pure domain policies/services
    event/                  # Domain events
    exception/              # Domain failures
  application/
    port/in/                # Input ports: one user/system intention
    port/out/               # Required external capabilities
    command/                # Framework-free commands
    query/                  # Framework-free queries
    result/                 # Framework-free results
    usecase/                # Application orchestration
  adapter/
    in/web/                 # Controllers, web DTOs, mappers
    in/messaging/           # Event consumers
    in/scheduling/          # Scheduled triggers
    out/persistence/        # JPA entities/repos/mappers/adapters
    out/security/           # Password, JWT, random-token adapters
    out/messaging/          # Outbox/event publishers
    out/integration/        # External service adapters
  configuration/           # Spring wiring only

src/main/resources/
  db/migration/             # Flyway migrations

src/test/java/com/microsoft/itechwx/<boundedcontext>/
  domain/
  application/
  adapter/
```

Rules:

- Java packages use lowercase names; classes use `PascalCase`.
- Keep files focused. Avoid dump packages such as `common`, `utils`,
  `helpers`, or a generic `services` package with unclear ownership.
- Name use cases by intention, for example `RegisterAccount`,
  `ReserveInventory`, or `FinalizeOrderPayment`.
- Bounded contexts must not import another context's domain model or persistence
  classes. Integrate through input ports, published contracts, or integration
  events.
- Shared code is limited to stable technical primitives. Do not move business
  rules into `shared` merely to resolve a dependency cycle.
- Package cycles are architecture defects and must not be hidden by Spring
  wiring.

---

## 4. Layer Rules

### Backend Java and Hexagonal layers

| Layer | Allowed | Forbidden |
| --- | --- | --- |
| Inbound adapter | DTO validation, authenticated actor extraction, mapping, response status | Business invariants, JPA access, Redis commands, vendor SDK calls |
| Input port | Stable use-case contract using application/domain types | Spring MVC, HTTP, JPA, vendor types |
| Application use case | Workflow orchestration, authorization, transactions, ports | HTTP response types, SQL, SDK calls, entity setters that bypass domain behavior |
| Domain | Aggregates, value objects, invariants, state transitions, pure policies, domain events | Spring, JPA, Jackson, Jakarta Validation, HTTP, Redis, Kafka, external clients |
| Output port | Small domain-shaped capability contract | Spring Data interfaces, SQL strings, provider DTOs |
| Persistence adapter | JPA/Spring Data usage and domain/persistence mapping | Raw request DTOs, authorization shortcuts, business-policy decisions |
| Security/integration adapter | Password encoding, JWT, secure random, provider calls | Account/order/inventory policy decisions |
| Event consumer | Idempotent async processing | Trusting payloads without validation/scope |

### Dependency rule

Allowed dependency direction:

```text
adapter/in  ─┐
configuration├──> application ───> domain
adapter/out ─┘          │
                        └──> port/out abstractions
```

Mandatory rules:

- Domain code is plain Java and compiles without Spring or persistence
  annotations.
- Application commands, queries, results, and ports are framework-free.
- Jakarta Validation annotations belong on inbound DTOs. Domain constructors
  and factories still enforce business invariants because use cases may be
  called without HTTP.
- JPA entities are persistence models in `adapter/out/persistence`; they are
  not aggregate roots merely because they use `@Entity`.
- Persistence adapters map between JPA entities and domain aggregates. Do not
  return a JPA entity through an application port.
- Spring annotations belong in adapters/configuration. `@Transactional` is
  permitted on a concrete application use-case implementation as a pragmatic
  exception because the use case owns the consistency boundary; never put it
  on domain types.
- `Clock`, id generation, password hashing, token signing, secure randomness,
  messaging, and external I/O must be injected through a port when behavior or
  testing depends on them.

### Aggregates and consistency

- An aggregate root is the only mutation entry point for members of its
  aggregate.
- Constructors/factories establish a valid initial state; methods implement
  named state transitions and reject invalid ones with typed failures.
- Prefer immutable value objects for identifiers, email, money, quantity,
  status-specific values, and other concepts with validation or behavior.
- Repository interfaces operate on aggregate roots or focused projections, not
  database tables.
- Prefer one transaction per aggregate. If a use case changes multiple
  aggregates, document why strong consistency is required or use domain events
  and compensation.
- Domain events describe facts that already happened. Integration events are
  stable external contracts and may be derived from domain events through an
  outbox after persistence succeeds.
- Uniqueness spanning all aggregates, such as normalized email uniqueness,
  requires a database constraint. A repository existence check alone is not
  race-safe.

### Cross-context workflows

- A bounded context owns its model and tables. Another context may keep only a
  local reference or projection needed for its own work.
- Cross-context orchestration belongs in an application workflow, not inside an
  aggregate.
- Synchronous modular-monolith collaboration uses the target context's input
  port. Asynchronous collaboration uses versioned, validated, idempotent
  integration events.
- Shop onboarding is not purely an Identity use case: Identity creates the
  account/credential, while Shop creates the business profile. An onboarding
  workflow coordinates them without importing the Shop aggregate into
  Identity.
- External side effects such as verification email and notification delivery
  run after commit or through an outbox.

### Frontend

| Layer | Allowed | Forbidden |
| --- | --- | --- |
| Route/page | Layout shell, route params, provider boundaries | Business calculations, ad hoc fetch calls |
| Feature component | UI composition, local UI state | Direct `fetch`, direct auth token parsing |
| Form/table model | Zod schema, field mapping, filters | Backend DTO mutation outside typed clients |
| API client | API calls, request/response typing | React state, UI behavior |

### Domain boundaries

- Auth owns identity, sessions, tokens, and permission primitives.
- User/customer modules own buyer profile and private buyer data.
- Shop modules own seller/shop profile and shop-owned settings.
- Catalog owns product, SKU, category, and public product state.
- Inventory owns stock, reservations, stock movements, and stock consistency.
- Cart owns temporary buyer selection.
- Checkout owns pricing review, discount application, shipping calculation, and reservation start.
- Order owns confirmed purchase state.
- Payment owns provider integration, payment status, idempotency, and webhook handling.
- Notification owns delivery state, not core business state.

Do not let one module silently mutate another module's critical state without an explicit use case, repository contract, or domain event.

---

## 5. Backend Java And Spring Boot Standards

### Controller rules

- One controller per resource or bounded route group.
- Validate all body, params, query, and headers before use.
- Use `@Valid` and constrained web DTOs at the HTTP boundary, then map to a
  framework-free application command.
- Never inject JPA repositories, `EntityManager`, Redis clients, payment SDKs,
  storage clients, email/SMS clients, or raw HTTP clients into controllers.
- Return stable response shapes. Do not leak database records directly.
- Use authenticated actor context from trusted guards/decorators.
- Do not trust IDs from the client when they can be derived from auth context.

```java
@PostMapping
ResponseEntity<ProductResponse> create(
        @Valid @RequestBody CreateProductRequest request,
        @AuthenticationPrincipal AuthenticatedShop actor) {
    var command = productWebMapper.toCommand(request, actor.shopId());
    var result = createProductUseCase.handle(command, actor);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(productWebMapper.toResponse(result));
}
```

### Use case rules

- A use case represents one user/system action.
- Use cases own transaction boundaries when multiple writes must succeed or fail together.
- Use cases call domain policies before persistence changes.
- Use cases return domain/result objects, not framework response types.
- Use idempotency keys for payment webhooks, retries, imports, and event consumers.
- Use explicit actor context: `user`, `shop`, `admin`, or `system`.
- Do not combine unrelated actions into manager/facade classes with broad
  mutable APIs.
- Command-side use cases change state; query-side use cases return focused
  projections and must still enforce scope.

### Repository rules

- Every shop-owned repository method must accept `shopId` explicitly.
- Every user-private repository method must accept `userId` explicitly.
- Do not use optional scope for scoped data.
- If admins need global reads, create a separate method with a name that says so.
- Do not return columns the caller does not need.
- Use explicit order for paginated lists.
- Use cursor pagination for high-volume lists where offset becomes expensive.

```java
Optional<Product> findByIdForShop(ShopId shopId, ProductId productId);

Optional<Cart> findByUserId(UserId userId);

// Bad for scoped data: ownership is impossible to prove.
Optional<Product> findById(ProductId productId);
```

### Error handling

Use typed errors internally and map once at the HTTP boundary.

| Situation | Error code shape | HTTP |
| --- | --- | --- |
| Invalid input | `VALIDATION_ERROR` | 400 |
| Missing auth | `UNAUTHENTICATED` | 401 |
| Permission denied | `FORBIDDEN` | 403 |
| Scoped resource not found | `NOT_FOUND` | 404 |
| Duplicate unique value | `DUPLICATE_CODE` or resource-specific | 409 |
| Optimistic lock conflict | `VERSION_CONFLICT` | 409 |
| Insufficient stock | `INSUFFICIENT_STOCK` | 409 |
| Reservation expired | `RESERVATION_EXPIRED` | 409 |
| Invalid order state transition | `INVALID_ORDER_STATE` | 409 |
| Rate limit exceeded | `RATE_LIMIT_EXCEEDED` | 429 |
| Downstream unavailable | `DOWNSTREAM_UNAVAILABLE` | 503 |

Do not:

- Return stack traces to clients.
- Swallow exceptions and return partial success unless the endpoint contract says so.
- Convert everything to `500`.
- Log full tokens, request bodies with secrets, passwords, payment secrets, webhook secrets, or raw auth headers.

### Configuration

- Bind configuration at startup with validated `@ConfigurationProperties`.
- Fail fast on missing production secrets.
- Keep local defaults safe and obviously non-production.
- Do not scatter `System.getenv`, `System.getProperty`, or `@Value` throughout
  business code. Inject typed configuration into adapters/configuration.
- Configuration names use uppercase snake case.
- Do not expose configuration values in logs unless they are explicitly safe.

---

## 6. Auth And Authorization Standards

### Principal types

The application may have multiple authenticated principal types:

| Principal | Scope |
| --- | --- |
| `user` | Buyer/customer account |
| `shop` | Seller/business account |
| `admin` | Platform operator |
| `system` | Internal job/event processor |

Rules:

- Keep principal type explicit in guards, decorators, and service inputs.
- Do not allow a user token to access shop-only APIs.
- Do not allow a shop token to access user-private APIs unless the endpoint explicitly allows it.
- Admin access must be explicit and protected by admin permission checks.
- System jobs must carry safe scope and reason metadata.

### JWT payloads

Current expected shapes may include:

```json
{
  "sub": "userId",
  "role": "user",
  "email": "user@example.invalid",
  "iat": 0,
  "exp": 0
}
```

```json
{
  "sub": "shopId",
  "role": "shop",
  "permissions": [],
  "iat": 0,
  "exp": 0
}
```

Rules:

- Verify token signature, issuer/audience if configured, expiration, and subject.
- Do not trust decoded token data without verification.
- Do not accept client-provided role or permissions outside verified auth context.
- Keep authenticated `accountId` distinct from business scope identifiers such
  as `userId` and `shopId`; do not silently overload them.
- Refresh tokens must be rotated and revocable. Store only a hash or other
  non-reversible verifier, never the raw bearer token.
- Reuse of a rotated refresh token revokes the affected session/token family
  and creates an audit signal.
- Passwords must be hashed with an approved password hashing algorithm.

### Identity reference workflow

Register account:

```text
validated web request
  -> RegisterAccount input port
  -> normalize EmailAddress + validate PasswordPolicy
  -> advisory duplicate lookup
  -> hash password through PasswordHashPort
  -> Account.register(...)
  -> persist account/credential in one transaction
  -> rely on database uniqueness for race safety
  -> append AccountRegistered outbox event
  -> commit
  -> send verification notification asynchronously
```

Login and refresh:

```text
rate limit + normalize identifier
  -> load account and credential
  -> return enumeration-resistant credential error when invalid
  -> verify account state and password
  -> record success/failure policy
  -> create or rotate Session
  -> store refresh-token hash
  -> issue access and refresh token
  -> emit safe audit evidence
```

Identity commands must not contain shop business profile, tax, currency, theme,
or notification-preference fields. A `RegisterShop` endpoint may accept a
composite boundary request, but an onboarding application workflow must split
it into an Identity command and a Shop command.

### Authorization decision tree

```text
Is the route public?
  |
  +-- yes -> still validate input and rate limit
  |
  +-- no -> is the caller authenticated?
        |
        +-- no -> 401
        |
        +-- yes -> does principal type allow this route?
              |
              +-- no -> 403
              |
              +-- yes -> does role/permission allow action?
                    |
                    +-- no -> 403
                    |
                    +-- yes -> is the resource in caller scope?
                          |
                          +-- no -> 403 or 404 according to endpoint contract
                          |
                          +-- yes -> allow
```

---

## 7. Ecommerce Domain Standards

### Product and catalog

- Shop-created products must be scoped by `shopId`.
- Public product reads may omit auth, but must only expose public-safe fields.
- Product creation and updates require shop ownership or admin permission.
- Product status transitions must be explicit: draft, active, inactive, archived, rejected, etc.
- SKU/variant rules must be validated consistently.
- Do not expose supplier/private shop notes in public product APIs.

### Inventory

- Inventory belongs to a product/SKU and must be scoped through `shopId`.
- Stock changes must be atomic.
- Use transactions or atomic update predicates for stock decrement/increment.
- Do not rely on stale in-memory stock values.
- Record stock movements for important state changes where audit/debugging matters.
- Prevent negative stock unless the business explicitly supports backorders.

Bad:

```typescript
const inventory = await inventoryRepo.findBySkuId(skuId);
inventory.stock -= quantity;
await inventoryRepo.save(inventory);
```

Good:

```typescript
await inventoryRepo.reserveStock({
  shopId,
  skuId,
  quantity,
  reservationId,
});
```

The repository implementation should enforce scope and stock availability in the same transaction or atomic predicate.

### Cart

- Cart belongs to `userId`.
- Cart item product/SKU data must be revalidated during checkout.
- Cart prices are display estimates, not payment source of truth.
- Do not trust client-submitted cart totals.
- Removed, inactive, or out-of-stock products must be handled during checkout review.

### Checkout

- Checkout review calculates authoritative totals on the server.
- Validate product/SKU status, shop availability, stock, discount eligibility, shipping rules, and user ownership.
- Use transaction for creating reservation/order records when consistency depends on multiple writes.
- Never trust client-provided subtotal, total, discount amount, shipping fee, or final payable amount.
- Return enough detail for the client to explain invalid items without leaking private shop data.

### Inventory reservation

- Reservation creation must be atomic with stock availability checks.
- Reservation has a clear expiration time.
- Expired unpaid reservations must be released safely.
- Release must be idempotent.
- Payment success must consume/finalize the reservation exactly once.
- Payment failure, cancellation, or timeout must release the reservation exactly once.
- Do not double-release or double-consume stock.

### Order

- Order belongs to `userId` and contains shop-scoped items.
- Shop-side order reads must be scoped by `shopId`.
- User-side order reads must be scoped by `userId`.
- State transitions must be explicit and validated.
- Invalid transitions return typed errors.
- Multi-shop orders should be modeled deliberately. Do not accidentally let one shop modify another shop's order line.

Example order state transition policy:

```text
pending_payment -> paid -> processing -> shipped -> completed
pending_payment -> cancelled
paid -> refunded / partially_refunded
processing -> cancelled only if business rules allow it
```

### Discounts and coupons

- Discount eligibility is calculated server-side.
- Validate ownership, status, time window, usage limits, product/category/shop scope, minimum order value, and user restrictions.
- Do not trust client-provided discount amounts.
- Usage counters that affect money or limits must be transactionally safe.
- Prevent double-use when a coupon is single-use or limited-use.

### Payment

- Do not store raw card data.
- Do not trust client-provided payment status.
- Verify payment provider webhook signatures.
- Process payment webhooks idempotently.
- Use transactions/locks for order payment finalization and refund state changes.
- Store provider identifiers safely; do not log full sensitive payment data.
- A repeated payment event must not create duplicate orders, duplicate stock consumption, or duplicate notifications.

### Comments, reviews, and messages

- Review creation should be tied to a completed order if the business requires verified purchases.
- User-generated content must be validated and sanitized before display.
- Shop replies must be scoped by `shopId`.
- User messages must be scoped by the conversation participants.
- Do not expose private user/shop metadata in public reviews or messages.

---

## 8. Frontend Standards

### UI system

Use the existing UI stack. If the project uses shadcn/ui, Tailwind, TanStack Query, and React Hook Form, keep using them. Do not introduce a parallel design system without approval.

| Use case | Preferred |
| --- | --- |
| Primary action | Existing Button component / shadcn Button |
| Secondary/destructive action | Existing variants / AlertDialog for destructive confirmation |
| Text input | Existing Input / Form field |
| Select/combo | Existing Select / Combobox pattern |
| Data grid | TanStack Table + existing table primitives |
| Modal | Dialog / AlertDialog |
| Toast | Existing toast/sonner setup |
| Icons | Existing icon library |

### UI hard constraints

| Must | Must not |
| --- | --- |
| Use semantic Tailwind tokens or design-system variables | Inline random hex colors across components |
| Use TanStack Query or project-approved server state library | Scatter `useEffect(fetch())` across pages |
| Use Zod/class-validator-compatible schemas where shared | Trust unvalidated form data |
| Show loading/empty/error/permission states | Leave blank screens during async work |
| Keep page components as shells | Put whole feature flows in one page file |
| Use accessible labels and keyboard behavior | Build custom controls without a11y semantics |

### Frontend feature shape

```text
features/<feature>/
  api/
    <feature>.client.ts
    <feature>.queries.ts
  schemas/
    <feature>.schema.ts
  components/
    <feature>-table.tsx
    <feature>-form.tsx
  pages/
    <feature>-page.tsx
  tests/
```

### Frontend security

- Do not parse auth tokens in random components.
- Do not hide authorization only in the frontend. Backend must enforce it.
- Do not store secrets in localStorage/sessionStorage.
- Treat prices, totals, discount amounts, stock counts, and order status from the client as display-only.
- Revalidate all money and inventory logic on the server.

---

## 9. Data And Database Standards

### Schema rules

- Use the ORM/query layer already used by the current module.
- The current persistence standard is Spring Data JPA/Hibernate. Do not mix
  JPA, jOOQ, JDBC templates, or ad hoc SQL inside the same bounded context
  unless the exception is explicitly designed and documented.
- Keep JPA entities in persistence adapters and map them to domain aggregates
  or query projections.
- Use explicit column names that match API/domain naming where practical.
- Use `shop_id` on shop-owned tables.
- Use `user_id` on user-private tables.
- Prefer `created_at`, `updated_at`, and `deleted_at` timestamps where soft delete is needed.
- Store metadata in JSON/JSONB only when the shape is intentionally flexible.
- Do not use JSON/JSONB to avoid modeling core business data.
- Add check constraints for values with hard invariants when supported.

### Migration rules

- One migration per logical change.
- Include indexes in the same migration when the new query pattern requires them.
- Never modify an old migration after it has been shared. Add a new migration.
- Backfill large tables with a deliberate batching plan.
- Production-destructive migrations require a rollback or export plan.
- Avoid nullable columns for required domain invariants unless there is a migration/backfill reason.

### Query rules

| Requirement | Standard |
| --- | --- |
| Shop-owned row | Query includes `shop_id` or equivalent scoped parent |
| User-private row | Query includes `user_id` or equivalent scoped parent |
| Admin/global read | Separate explicit admin method |
| Lists | Stable order + pagination |
| Search | Use appropriate indexes where relevant |
| Inventory mutation | Atomic predicate, lock, or transaction |
| Audit trail | Record actor, action, entity type, entity id, safe old/new values where required |
| Imports | Use staging/validation before mutation for large files |

### Transaction boundaries

Use transactions for:

- Shop registration approval if it creates multiple records.
- Product creation when product, SKU, inventory, and media records must be consistent.
- Inventory reservation and release.
- Checkout review finalization when it writes reservation/order state.
- Order creation.
- Payment success/failure/cancellation handling.
- Refund and return workflows.
- Discount usage counter updates.
- Bulk import finalization.
- Any operation that updates both business state and audit logs.

---

## 10. API Contract Standards

### Request/response naming

- Follow the existing API naming convention first.
- If no convention exists, use `camelCase` JSON fields consistently.
- IDs are strings in JSON and use the project's UUID convention internally.
- Timestamps are ISO 8601 strings with timezone.
- Money values must include currency context or be clearly documented as minor units.
- Pagination responses include `data` and `meta`.

```json
{
  "data": [],
  "meta": {
    "page": 1,
    "limit": 25,
    "total": 0,
    "totalPages": 0
  }
}
```

### Error response

Use the existing error shape first. If no standard exists, prefer:

```json
{
  "statusCode": 409,
  "error": "Conflict",
  "code": "INSUFFICIENT_STOCK",
  "message": "Not enough stock is available for this item.",
  "requestId": "req_..."
}
```

### Versioning

- Public APIs should be versioned, for example `/v1/api` or `/api/v1`, following the current project convention.
- Breaking response changes require a new version or compatibility layer.
- Internal module refactors do not require API version changes.
- Do not silently change response field names used by frontend or external clients.

---

## 11. Events, Queues, And Realtime Standards

### Event rules

- Use events/queues for slow or background workflows.
- Do not block checkout/order APIs on non-critical notifications.
- Event payloads must not contain secrets.
- Event payloads must include enough scope to process safely: `userId`, `shopId`, `orderId`, or system context where relevant.
- Event consumers must validate payloads before processing.
- Event consumers should be idempotent where possible.
- Failed async workflows should be logged with enough context but without sensitive data.

### Recommended event names

Use consistent domain-style names:

```text
user.registered
shop.registered
product.created
inventory.reserved
inventory.released
order.created
order.paid
order.cancelled
payment.succeeded
payment.failed
notification.requested
message.sent
```

### Realtime/chat

- Authenticate socket connections.
- Authorize room/channel subscription.
- Do not let clients choose arbitrary room names for private conversations.
- Message reads/writes must verify participant scope.
- Realtime notifications should not become the source of truth for order/payment state.

---

## 12. Testing Standards

### Test hierarchy

| Test type | Purpose |
| --- | --- |
| Unit | Pure policies, calculators, validators, mappers |
| Integration | DB, Redis, auth, repositories, queues, payment webhook handlers |
| Contract | API request and response compatibility |
| E2E | User, shop, and admin workflows through real API paths |
| Load | Checkout, inventory, search, order creation, connection pools |
| Security | IDOR, auth bypass, permission denial, rate limits, webhook signatures |

### Required tests by feature

| Change | Required coverage |
| --- | --- |
| Auth or permissions | Unit + integration + negative authorization cases |
| Shop-owned resource | Cross-shop access denial tests |
| User-private resource | Cross-user access denial tests |
| Product/SKU | Validation + shop ownership tests |
| Inventory | Concurrent reservation/decrement tests where possible |
| Checkout/order | Success path + invalid item + insufficient stock + expired reservation |
| Payment/webhook | Signature verification + idempotency + repeated event handling |
| Discount/coupon | Valid, expired, over-limit, wrong shop/product/category, repeated use |
| Bulk import/export | Validation, partial failure, dry run, scope |
| UI form | Validation + loading/error/success states |

### Must not

- Do not skip failing tests to finish a feature.
- Do not delete tests without replacing the covered behavior.
- Do not snapshot huge UI trees as the only assertion.
- Do not mock the authorization layer out of integration tests that claim to cover API behavior.
- Do not claim tests passed if they were not run.

---

## 13. Observability Standards

### Logs

Structured logs should include:

- `request_id`
- `service`
- `route`
- `method`
- `status`
- `duration_ms`
- `user_id` when available
- `shop_id` when available
- `actor_type` when available
- `actor_id` when available
- safe error code

Do not log:

- Authorization headers.
- Passwords.
- Refresh tokens.
- JWTs.
- Webhook secrets.
- Full payment identifiers.
- Full request bodies containing sensitive data.
- Database URLs with passwords.

### Metrics

Track where practical:

- Request count and latency by route/service/status.
- Auth failures and rate limit events.
- Checkout review latency.
- Order creation success/failure count.
- Inventory reservation success/failure/expiry count.
- Payment webhook success/failure/idempotency conflict count.
- Queue job duration and failure count.
- Search latency by catalog size bucket where possible.

---

## 14. New Feature Order

Use this order unless there is a strong reason not to:

1. Read relevant requirements and test cases.
2. Query Graphify when `graphify-out/graph.json` exists and inspect the current
   bounded context.
3. Identify actor type, user/system intention, ownership scope, and consistency
   boundary.
4. Define the input port, command/query, result, domain invariants, aggregate
   boundary, state transitions, and events.
5. Define small output ports for capabilities the use case needs.
6. Write domain and application tests for success, invariant failure,
   authorization denial, and relevant races/retries.
7. Implement the domain and application use case.
8. Add persistence/security/integration adapters, mappings, and migrations.
9. Add inbound DTO validation, mapper, controller/consumer, and contract tests.
10. Add UI or cross-context integration if needed.
11. Update `SECURITY.md`, `LIBRARY.md`, API docs, or architecture docs when the
    source of truth changes.
12. Run formatting, tests, build, and `graphify update .`.

---

## 15. Anti-patterns

| Do not | Do |
| --- | --- |
| `findById(id)` for shop-owned data | `findByIdForShop(shopId, id)` |
| `findOrderById(orderId)` for user order history | `findOrderForUser(userId, orderId)` |
| Controller calls payment SDK directly | Use payment use case and provider adapter |
| Controller updates inventory directly | Use inventory reservation/use case |
| Client sends final order total | Server recalculates authoritative total |
| Client sends payment status | Verify provider webhook/server callback |
| Frontend uses `fetch` in random components | Use typed API client + TanStack Query/project convention |
| One `common` or `utils` package for everything | Name packages/modules by capability |
| Reuse DTOs as domain entities | Map DTOs to domain/application models |
| Put `@Entity` on domain aggregates | Use persistence entities plus explicit mappers |
| Identity creates a Shop aggregate directly | Use an onboarding workflow and the Shop input port/event |
| Store a raw refresh token | Store a token hash and rotate/revoke sessions |
| Catch and ignore errors | Convert to typed failure or log and rethrow |
| Use JSON/JSONB for all business data | Model stable fields relationally |
| Public route by omission | Public route by explicit declaration |
| Background job without scope | Job payload includes `userId`, `shopId`, or system context |
| Shop query without `shopId` | Scope query by `shopId` or explicit admin method |
| User-private query without `userId` | Scope query by `userId` |

---

## 16. Final Checklist

Before a change is done:

- [ ] Actor type is explicit: user, shop, admin, or system.
- [ ] The bounded context and aggregate ownership are explicit.
- [ ] Domain and application contracts do not depend on Spring/JPA/HTTP/vendor
      types, except the documented transaction annotation exception.
- [ ] Cross-context changes use input ports or integration events instead of
      importing another context's aggregate.
- [ ] Shop scope is explicit where needed.
- [ ] User scope is explicit where needed.
- [ ] Inputs are validated.
- [ ] Authorization is enforced server-side.
- [ ] Controllers are thin.
- [ ] Business workflows live in use cases/application services.
- [ ] Inventory consistency is preserved.
- [ ] Money, discount, stock, and payment status are not trusted from the client.
- [ ] Transactions are used where multiple writes must be atomic.
- [ ] Errors are typed and mapped once.
- [ ] Logs redact sensitive data.
- [ ] Tests cover the changed risk.
- [ ] New dependencies are approved or documented in `LIBRARY.md`.
- [ ] Security-sensitive behavior is reflected in `SECURITY.md`.
- [ ] Agent-facing instructions still point to the right files.
- [ ] Graphify was updated after code or module-structure changes.

---

## Related

- `AGENTS.md`
- `SECURITY.md`
- `LIBRARY.md`
