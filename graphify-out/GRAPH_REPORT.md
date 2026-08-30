# Graph Report - itechwx  (2026-08-30)

## Corpus Check
- 63 files · ~23,434 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 496 nodes · 691 edges · 56 communities (53 shown, 3 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 50 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `8877757d`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 33|Community 33]]
- [[_COMMUNITY_Community 34|Community 34]]
- [[_COMMUNITY_Community 35|Community 35]]
- [[_COMMUNITY_Community 36|Community 36]]
- [[_COMMUNITY_Community 37|Community 37]]
- [[_COMMUNITY_Community 38|Community 38]]
- [[_COMMUNITY_Community 39|Community 39]]
- [[_COMMUNITY_Community 40|Community 40]]
- [[_COMMUNITY_Community 41|Community 41]]
- [[_COMMUNITY_Community 42|Community 42]]
- [[_COMMUNITY_Community 43|Community 43]]
- [[_COMMUNITY_Community 49|Community 49]]
- [[_COMMUNITY_Community 50|Community 50]]
- [[_COMMUNITY_Community 51|Community 51]]
- [[_COMMUNITY_Community 52|Community 52]]
- [[_COMMUNITY_Community 53|Community 53]]

## God Nodes (most connected - your core abstractions)
1. `Ecommerce Security Standard` - 29 edges
2. `Ecommerce Coding Standards` - 18 edges
3. `Shop` - 17 edges
4. `ShopSettings` - 17 edges
5. `Agent Instructions` - 12 edges
6. `Library And Dependency Standard` - 12 edges
7. `itechwx` - 12 edges
8. `String` - 10 edges
9. `String` - 10 edges
10. `6. Implementation Rules` - 10 edges

## Surprising Connections (you probably didn't know these)
- `ShopAuthentication` --inherits--> `AbstractShop`  [EXTRACTED]
  src/main/java/com/microsoft/itechwx/identity/application/service/ShopAuthentication.java → src/main/java/com/microsoft/itechwx/identity/application/service/AbstractShop.java
- `ShopRegister` --inherits--> `AbstractShop`  [EXTRACTED]
  src/main/java/com/microsoft/itechwx/identity/application/service/ShopRegister.java → src/main/java/com/microsoft/itechwx/identity/application/service/AbstractShop.java
- `CreateShopService` --implements--> `CreateShopUseCase`  [EXTRACTED]
  src/main/java/com/microsoft/itechwx/shop/application/service/CreateShopService.java → src/main/java/com/microsoft/itechwx/shop/application/port/in/CreateShopUseCase.java
- `CreateShopService` --implements--> `ValidateShopRegistrationUseCase`  [EXTRACTED]
  src/main/java/com/microsoft/itechwx/shop/application/service/CreateShopService.java → src/main/java/com/microsoft/itechwx/shop/application/port/in/ValidateShopRegistrationUseCase.java
- `RecordingShopRepository` --implements--> `ShopRepository`  [EXTRACTED]
  src/test/java/com/microsoft/itechwx/shop/application/service/CreateShopServiceTest.java → src/main/java/com/microsoft/itechwx/shop/application/service/CreateShopService.java

## Import Cycles
- None detected.

## Communities (56 total, 3 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.06
Nodes (31): 10. Money, Inventory, And Order Integrity, 11. Inventory And Reservation Security, 12. Cart, Checkout, And Order Security, 13. Discounts And Coupons, 14. Payment And Webhook Security, 15. Events, Queues, Notifications, And Realtime, 16. Comments, Reviews, Messages, And User Content, 17. File Upload, Download, Import, And Export (+23 more)

### Community 1 - "Community 1"
Cohesion: 0.18
Nodes (10): 13. Observability Standards, 14. New Feature Order, 15. Anti-patterns, 16. Final Checklist, 2. Source Of Truth Order, 3. Repository Shape, Ecommerce Coding Standards, Logs (+2 more)

### Community 2 - "Community 2"
Cohesion: 0.10
Nodes (19): 10. Dependency Checklist, 1. Scope, 2. Current Approved Baseline, 3. Mandatory Dependency Workflow, 4. Hexagonal Placement Rules, 5. Approved Usage By Capability, 6. Version And Upgrade Policy, 7. Graphify (+11 more)

### Community 3 - "Community 3"
Cohesion: 0.08
Nodes (24): 10. Graphify Knowledge Graph, 1. Mission, 2. Required Reading, 3. Project Facts, 4. How To Work, 5. Documentation Rules, 6. Implementation Rules, 7. Security Rules For Agents (+16 more)

### Community 5 - "Community 5"
Cohesion: 0.25
Nodes (4): DuplicateAccountException, InvalidCredentialsException, RuntimeException, Throwable

### Community 6 - "Community 6"
Cohesion: 0.20
Nodes (10): 7. Ecommerce Domain Standards, Cart, Checkout, Comments, reviews, and messages, Discounts and coupons, Inventory, Inventory reservation, Order (+2 more)

### Community 7 - "Community 7"
Cohesion: 0.09
Nodes (27): AccountPort, AccountProfile, AuthenticationShopUseCase, InvalidCredentialsException, RefreshToken, RegisterShopUseCase, AbstractShop, ShopAuthentication (+19 more)

### Community 8 - "Community 8"
Cohesion: 0.42
Nodes (5): IdentityConfiguration, PasswordEncoder, Bean, Clock, PasswordHashPort

### Community 9 - "Community 9"
Cohesion: 0.29
Nodes (7): 4. Layer Rules, Aggregates and consistency, Backend Java and Hexagonal layers, Cross-context workflows, Dependency rule, Domain boundaries, Frontend

### Community 10 - "Community 10"
Cohesion: 0.24
Nodes (10): LoginShopRequest, LoginShopResponse, PostMapping, RegisterShopRequest, RegisterShopResponse, AuthenticationShopUseCase, RegisterShopCommand, RegisterShopUseCase (+2 more)

### Community 11 - "Community 11"
Cohesion: 0.33
Nodes (6): 1. Operating Principles, Non-negotiables, Preferred backend feature flow, Preferred ecommerce workflow flow, Preferred frontend feature flow, SOLID + DRY + Shop/User Safety

### Community 12 - "Community 12"
Cohesion: 0.33
Nodes (6): 5. Backend Java And Spring Boot Standards, Configuration, Controller rules, Error handling, Repository rules, Use case rules

### Community 13 - "Community 13"
Cohesion: 0.40
Nodes (5): 8. Frontend Standards, Frontend feature shape, Frontend security, UI hard constraints, UI system

### Community 14 - "Community 14"
Cohesion: 0.40
Nodes (5): 9. Data And Database Standards, Migration rules, Query rules, Schema rules, Transaction boundaries

### Community 15 - "Community 15"
Cohesion: 0.60
Nodes (3): RegisterShopUseCase, RegisterShopCommand, RegisterShopResult

### Community 17 - "Community 17"
Cohesion: 0.10
Nodes (16): ShopSettings, CreateShopUseCase, ValidateShopRegistrationUseCase, CreateShopService, CreatedShop, CreateShopCommand, CreateShopCommand, Clock (+8 more)

### Community 18 - "Community 18"
Cohesion: 0.20
Nodes (8): AccountType, toString(), Account, Override, String, AccountAuthentication, Instant, UUID

### Community 19 - "Community 19"
Cohesion: 0.29
Nodes (7): AuthMethod, AccountAuthentication, Account, DeviceSession, Instant, String, UUID

### Community 20 - "Community 20"
Cohesion: 0.16
Nodes (11): ShopMembership, CreateShopServiceTest, RecordingShopRepository, ShopMembershipStatus, ShopRole, ShopRepository, Instant, UUID (+3 more)

### Community 21 - "Community 21"
Cohesion: 0.38
Nodes (5): DeviceSession, AccountAuthentication, Instant, String, UUID

### Community 22 - "Community 22"
Cohesion: 0.15
Nodes (12): Architecture, Build and test, Current persistence workflow, Current security workflow, Current status, Feature-development workflow, Identity registration workflow, itechwx (+4 more)

### Community 24 - "Community 24"
Cohesion: 0.33
Nodes (5): RefreshToken, DeviceSession, Instant, String, UUID

### Community 25 - "Community 25"
Cohesion: 0.50
Nodes (3): toString(), Override, String

### Community 26 - "Community 26"
Cohesion: 0.50
Nodes (3): toString(), Override, String

### Community 29 - "Community 29"
Cohesion: 0.13
Nodes (10): Shop, toString(), ShopMembership, ShopSettings, ShopStatus, Override, String, Instant (+2 more)

### Community 31 - "Community 31"
Cohesion: 0.18
Nodes (10): AtomicReference, AttemptWindow, Decision, RegistrationRateLimiter, RegistrationRateLimiterTest, Clock, Duration, Instant (+2 more)

### Community 32 - "Community 32"
Cohesion: 0.47
Nodes (5): ExceptionHandler, IdentityErrorResponse, IllegalArgumentException, ResponseEntity, IdentityExceptionHandler

### Community 33 - "Community 33"
Cohesion: 0.48
Nodes (4): AccountProfile, Account, Instant, String

### Community 34 - "Community 34"
Cohesion: 0.29
Nodes (7): FilterChain, HttpServletRequest, HttpServletResponse, OncePerRequestFilter, RegistrationRateLimitFilter, Override, RegistrationRateLimiter

### Community 35 - "Community 35"
Cohesion: 0.29
Nodes (8): HttpSecurity, JwtDecoder, SecurityConfiguration, SecurityFilterChain, Bean, Duration, RegistrationRateLimiter, RegistrationRateLimitFilter

### Community 36 - "Community 36"
Cohesion: 0.33
Nodes (5): FilterResult, RegistrationRateLimitFilterTest, RegistrationRateLimitFilter, String, Test

### Community 37 - "Community 37"
Cohesion: 0.40
Nodes (5): 6. Auth And Authorization Standards, Authorization decision tree, Identity reference workflow, JWT payloads, Principal types

### Community 38 - "Community 38"
Cohesion: 0.60
Nodes (3): AuthenticationShopUseCase, LoginShopCommand, LoginShopResult

### Community 39 - "Community 39"
Cohesion: 0.50
Nodes (3): toString(), Override, String

### Community 40 - "Community 40"
Cohesion: 0.50
Nodes (4): 10. API Contract Standards, Error response, Request/response naming, Versioning

### Community 41 - "Community 41"
Cohesion: 0.50
Nodes (4): 11. Events, Queues, And Realtime Standards, Event rules, Realtime/chat, Recommended event names

### Community 42 - "Community 42"
Cohesion: 0.50
Nodes (4): 12. Testing Standards, Must not, Required tests by feature, Test hierarchy

### Community 49 - "Community 49"
Cohesion: 0.50
Nodes (3): toString(), Override, String

### Community 50 - "Community 50"
Cohesion: 0.50
Nodes (3): toString(), Override, String

### Community 51 - "Community 51"
Cohesion: 0.50
Nodes (3): toString(), Override, String

### Community 52 - "Community 52"
Cohesion: 0.50
Nodes (3): toString(), Override, String

### Community 53 - "Community 53"
Cohesion: 0.50
Nodes (3): toString(), Override, String

## Knowledge Gaps
- **168 isolated node(s):** `String`, `IllegalArgumentException`, `String`, `Override`, `String` (+163 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Ecommerce Coding Standards` connect `Community 1` to `Community 37`, `Community 6`, `Community 40`, `Community 41`, `Community 42`, `Community 11`, `Community 9`, `Community 12`, `Community 13`, `Community 14`?**
  _High betweenness centrality (0.016) - this node is a cross-community bridge._
- **What connects `String`, `IllegalArgumentException`, `String` to the rest of the system?**
  _168 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.0625 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._
- **Should `Community 3` be split into smaller, more focused modules?**
  _Cohesion score 0.08 - nodes in this community are weakly interconnected._
- **Should `Community 7` be split into smaller, more focused modules?**
  _Cohesion score 0.08780487804878048 - nodes in this community are weakly interconnected._
- **Should `Community 17` be split into smaller, more focused modules?**
  _Cohesion score 0.09745293466223699 - nodes in this community are weakly interconnected._