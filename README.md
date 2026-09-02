# store-project

A REST API for an online store: product catalog, guest shopping carts, JWT-authenticated user accounts, and Stripe Checkout payments.

## Quick start

```bash
cp .env.example .env          # then fill in the values (see Configuration)
docker compose up -d          # starts MySQL 8.0 on port 3306
./mvnw spring-boot:run        # starts the API on port 8080
```

Interactive API docs: http://localhost:8080/swagger-ui.html

## Overview

The project is the backend half of a storefront. It exposes a JSON API covering
the full purchase path: browse products by category, build a cart without an
account, register and log in, check out through Stripe, and read back past
orders. Flyway migrations create the schema and seed a catalog of 12 categories
and 30 products, so a fresh database is usable immediately.

Authentication is stateless JWT. Login returns a short-lived access token in the
response body and sets a long-lived refresh token as an `HttpOnly` cookie scoped
to `/auth/refresh`. Every request is authenticated by default; each feature
package registers its own public or role-restricted routes through the
`SecurityRules` interface.

Scope limits worth knowing:

- **API only.** There is no frontend. `websiteUrl` points at an external site
  that hosts the Stripe success and cancel pages. `src/main/resources/static/`
  is empty and there is no `templates/` directory, so `GET /` has no view to
  render.
- **No admin product UI** — product writes are plain REST endpoints gated on the
  `ADMIN` role. Nothing in the app promotes a user to `ADMIN`; you set
  `users.role = 'ADMIN'` in the database by hand.
- **Carts are unauthenticated.** All `/carts/**` routes are public and a cart is
  addressed only by its UUID. Carts are not tied to a user account.

## Architecture

A single Spring Boot service in front of MySQL, with Stripe as the only external
dependency. Every request passes the JWT filter first, then the security chain
assembled from each feature's own `SecurityRules`, before reaching a controller.

```mermaid
flowchart TD
    client["HTTP client<br>browser · curl · Swagger UI"]

    subgraph boot["Spring Boot 4.1 · port 8080"]
        direction TB

        jwtf["JwtAuthenticationFilter<br>Bearer token → Authentication"]
        chain["SecurityFilterChain<br>SecurityRules, one per feature"]

        subgraph ctl["Controllers"]
            direction LR
            c1["/auth"]
            c2["/users"]
            c3["/products"]
            c4["/carts"]
            c5["/orders"]
            c6["/checkout"]
            c7["/admin"]
        end

        subgraph svc["Services"]
            direction LR
            s1["AuthService<br>JwtService"]
            s2["UserService"]
            s3["CartService"]
            s4["OrderService"]
            s5["CheckoutService"]
        end

        subgraph repo["Repositories · Spring Data JPA"]
            direction LR
            r1["User<br>Profile · Address"]
            r2["Product<br>Category"]
            r3["Cart"]
            r4["Order"]
        end

        gw["PaymentGateway interface<br>StripePaymentGateway"]
    end

    db[("MySQL 8.0<br>schema and seed data<br>from Flyway V1–V5")]
    stripe["Stripe<br>Checkout Sessions · webhooks"]

    client -->|HTTP| jwtf
    jwtf --> chain
    chain -->|authorized request| ctl
    ctl --> svc
    ctl -->|direct repository access| repo
    svc --> repo
    s5 --> gw
    gw -->|Stripe SDK| stripe
    stripe -. "payment_intent.* webhook" .-> c6
    repo -->|JDBC| db

    classDef outside fill:#334155,stroke:#0f172a,stroke-width:2px,color:#ffffff
    classDef security fill:#b45309,stroke:#0f172a,stroke-width:2px,color:#ffffff
    classDef controller fill:#1d4ed8,stroke:#0f172a,stroke-width:2px,color:#ffffff
    classDef service fill:#6d28d9,stroke:#0f172a,stroke-width:2px,color:#ffffff
    classDef repository fill:#0f766e,stroke:#0f172a,stroke-width:2px,color:#ffffff
    classDef gateway fill:#be123c,stroke:#0f172a,stroke-width:2px,color:#ffffff

    class client,db,stripe outside
    class jwtf,chain security
    class c1,c2,c3,c4,c5,c6,c7 controller
    class s1,s2,s3,s4,s5 service
    class r1,r2,r3,r4 repository
    class gw gateway

    style boot fill:#f1f5f9,stroke:#64748b,stroke-width:2px,color:#0f172a
    style ctl fill:#dbeafe,stroke:#1d4ed8,stroke-width:2px,color:#0f172a
    style svc fill:#ede9fe,stroke:#6d28d9,stroke-width:2px,color:#0f172a
    style repo fill:#ccfbf1,stroke:#0f766e,stroke-width:2px,color:#0f172a

    linkStyle 0,1,2,3,4,5,6,7,9 stroke:#64748b,stroke-width:2.5px
    linkStyle 8 stroke:#e11d48,stroke-width:2.5px
```

`ProductController` and `UserController` reach their repositories directly; the
other features go through a service. `CheckoutService` depends on the
`PaymentGateway` interface rather than Stripe, so the gateway is the only class
that touches the Stripe SDK.

### Checkout flow

The one path that spans all three systems. Note that the order is saved before
Stripe is called and rolled back if the session cannot be created, and that the
status only leaves `PENDING` when the webhook arrives.

```mermaid
sequenceDiagram
    participant C as Client
    participant CO as CheckoutController
    participant CS as CheckoutService
    participant DB as MySQL
    participant SG as StripePaymentGateway
    participant S as Stripe

    C->>CO: POST /checkout {cartId}
    CO->>CS: checkout
    CS->>DB: load cart with items
    CS->>DB: save Order, status PENDING
    CS->>SG: createCheckoutSession
    SG->>S: create Session, order_id in PaymentIntent metadata
    S-->>SG: checkout URL
    CS->>DB: clear cart
    CS-->>C: orderId + checkoutUrl

    Note over C,S: customer pays on Stripe's hosted page

    S->>CO: POST /checkout/webhook, signed
    CO->>CS: handWebhookEvent
    CS->>SG: parseWebhookRequest, verify signature
    SG-->>CS: PaymentResult with orderId and PAID or FAILED
    CS->>DB: update order status
```

## Tech stack

| Component          | Version                          | Role                                                            |
| ------------------ | -------------------------------- | --------------------------------------------------------------- |
| Java               | 21                               | Language level (`java.version` in `pom.xml`)                |
| Spring Boot        | 4.1.0                            | Application framework; parent POM for dependency versions       |
| Spring Web MVC     | (Boot-managed)                   | REST controllers                                                |
| Spring Data JPA    | (Boot-managed)                   | Repositories and entity mapping                                 |
| Spring Security    | (Boot-managed)                   | Authentication, role checks, BCrypt hashing                     |
| MySQL              | 8.0                              | Database (`mysql-connector-j` at runtime)                     |
| Flyway             | (Boot-managed) +`flyway-mysql` | Versioned schema migrations under`db/migration`               |
| jjwt               | 0.13.0                           | Signing and parsing access/refresh tokens (HMAC-SHA)            |
| Stripe Java        | 33.4.0                           | Checkout Session creation and webhook signature verification    |
| springdoc-openapi  | 3.1.0                            | OpenAPI 3 spec generation and Swagger UI                        |
| MapStruct          | 1.7.0.Beta2                      | Compile-time entity ↔ DTO mappers                              |
| Lombok             | (Boot-managed)                   | Constructor, getter, and builder generation                     |
| springboot4-dotenv | 5.1.0                            | Loads`.env` from the project root into the Spring environment |
| Thymeleaf          | (Boot-managed)                   | On the classpath; no templates are shipped                      |
| Maven Wrapper      | 3.9.16                           | Build; no local Maven install needed                            |

## Prerequisites

- **JDK 21** or newer.
- **Docker and Docker Compose**, or a MySQL 8.0 server you manage yourself.
- **A Stripe account** for the secret key and webhook signing secret. The
  application fails to start without them — they have no defaults.
- Maven is not required; use the bundled `./mvnw`.

## Installation

```bash
git clone <repository-url>
cd store-project

cp .env.example .env
# edit .env — see Configuration below

docker compose up -d          # MySQL 8.0, named volume store_data
./mvnw clean install          # compiles, runs annotation processors, runs tests
```

Flyway runs automatically at application startup, so the schema and seed data
are applied on the first run. To apply migrations without booting the app:

```bash
./mvnw flyway:migrate
```

Note that the `flyway-maven-plugin` block in `pom.xml` has its connection
details hardcoded (`jdbc:mysql://localhost:3306/store`, user `root`, password
`12345`) and ignores `.env`. Adjust it if your local database differs.

## Configuration

Configuration lives in `.env` at the project root, loaded by `springboot4-dotenv`.
Real environment variables work equally well.

| Variable                      | Required   | Default   | Used by                                                                 |
| ----------------------------- | ---------- | --------- | ----------------------------------------------------------------------- |
| `JWT_SECRET`                | Yes        | none      | HMAC key for signing access and refresh tokens. Startup fails if unset. |
| `STRIPE_SECRET_KEY`         | Yes        | none      | Stripe API key, applied to`Stripe.apiKey` at startup.                 |
| `STRIPE_WEBHOOK_SECRET_KEY` | Yes        | none      | Verifies the`Stripe-Signature` header on `POST /checkout/webhook`.  |
| `ROOT_PASSWORD`             | For Docker | none      | `MYSQL_ROOT_PASSWORD` in `docker-compose.yml`. Not read by the app. |
| `DB_NAME`                   | No         | `store` | Database name in the dev JDBC URL, and`MYSQL_DATABASE` in Compose.    |
| `DB_USER`                   | No         | `root`  | Dev datasource username.                                                |
| `DB_PASSWORD`               | No         | `12345` | Dev datasource password.                                                |
| `DB_PORT`                   | No         | `3306`  | Dev datasource port.                                                    |
| `SPRING_DATASOURCE_URL`     | Prod only  | none      | Full JDBC URL for the`prod` profile, credentials included.            |

`.env.example` is missing `STRIPE_WEBHOOK_SECRET_KEY`; add it to your `.env` or
webhook handling will fail at startup.

### Profiles

`spring.profiles.active` defaults to `dev` in `application.yml`.

- **`application-dev.yml`** — local MySQL with the defaults above, `show-sql`
  enabled, Tomcat access logging to stdout, `websiteUrl` of
  `http://localhost:4242`.
- **`application-prod.yml`** — datasource driven entirely by
  `SPRING_DATASOURCE_URL`, `websiteUrl` of `https://frontend.com:3000`
  (a placeholder — change it before deploying).

Token lifetimes are set in `application.yml`, not the environment:
`accessTokenExpiration` is 900 seconds and `refreshTokenExpiration` is 604800
seconds (7 days).

## Usage

### Development

```bash
./mvnw spring-boot:run
```

The API listens on port 8080 with the `dev` profile.

### Production

```bash
./mvnw clean package
SPRING_PROFILES_ACTIVE=prod \
SPRING_DATASOURCE_URL='jdbc:mysql://user:password@host:3306/store' \
java -jar target/store-project-1.0.0.jar
```

`spring.profiles.active: dev` is hardcoded in `application.yml`, so
`SPRING_PROFILES_ACTIVE` must be set explicitly to select `prod`.

### A minimal end-to-end run

```bash
# 1. Browse the seeded catalog (public)
curl http://localhost:8080/products
curl 'http://localhost:8080/products?categoryId=1'

# 2. Create a cart and add a product (public, no account needed)
CART=$(curl -s -X POST http://localhost:8080/carts | jq -r .id)
curl -X POST http://localhost:8080/carts/$CART/items \
  -H 'Content-Type: application/json' \
  -d '{"productId": 1}'

# 3. Register and log in
curl -X POST http://localhost:8080/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ada","email":"ada@example.com","password":"secret123"}'

TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"ada@example.com","password":"secret123"}' | jq -r .token)

# 4. Check out — returns a Stripe Checkout URL
curl -X POST http://localhost:8080/checkout \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d "{\"cartId\": \"$CART\"}"

# 5. Read back orders
curl http://localhost:8080/orders -H "Authorization: Bearer $TOKEN"
```

Authenticated requests use `Authorization: Bearer <accessToken>`. The refresh
token is set as a cookie by `/auth/login`; `POST /auth/refresh` reads it from
that cookie and returns a new access token.

### Route access

| Routes                                                        | Access                                |
| ------------------------------------------------------------- | ------------------------------------- |
| `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**` | Public                                |
| `GET /products/**`                                          | Public                                |
| `POST`/`PUT`/`DELETE /products/**`                      | `ADMIN`                             |
| `/carts/**`                                                 | Public                                |
| `POST /users`                                               | Public                                |
| `POST /auth/login`, `POST /auth/refresh`                  | Public                                |
| `POST /checkout/webhook`                                    | Public (verified by Stripe signature) |
| `/admin/**`                                                 | `ADMIN`                             |
| Everything else                                               | Authenticated                         |

### Stripe webhooks locally

An order is created with status `PENDING` and only changes when Stripe calls
back, so forward events to your local instance:

```bash
stripe listen --forward-to localhost:8080/checkout/webhook
```

Use the signing secret that `stripe listen` prints as `STRIPE_WEBHOOK_SECRET_KEY`.
The handler acts on `payment_intent.succeeded` and
`payment_intent.payment_failed`, reading `order_id` from the PaymentIntent
metadata.

## Project structure

```
src/main/java/com/akamed/storeproject/
├── StoreApplication.java   Spring Boot entry point
├── admin/                  Admin-only endpoints and their security rules
├── auth/                   JWT issuing/parsing, login, refresh, SecurityConfig
├── carts/                  Cart and CartItem entities, service, controller
├── common/                 SecurityRules interface, global exception handler, error DTO
├── orders/                 Order and OrderItem entities, ownership checks
├── payment/                Stripe gateway, checkout service, webhook handling
├── products/               Product and Category entities, catalog endpoints
└── users/                  User, Profile, Address, registration, UserDetailsService

src/main/resources/
├── application.yml         Shared config; sets the active profile
├── application-dev.yml     Local MySQL, SQL logging, access log
├── application-prod.yml    Env-driven datasource
├── db/migration/           Flyway migrations V1–V5 (V5 seeds the catalog)
└── static/                 Empty

src/test/java/              JUnit 5 tests
docker-compose.yml          MySQL 8.0 for local development
```

Each feature package owns its own security rules: a `@Component` implementing
`SecurityRules` that registers matchers, which `SecurityConfig` collects into a
list and applies in turn before the `anyRequest().authenticated()` fallback.
