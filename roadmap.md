# Backend Roadmap — Sistema de Inventario (Carpintería)
## Spring Boot 3.4.3 · Java 23 · MongoDB 7.x

> **Estado actual:** Proyecto scaffoldeado con `BackendApplication.java` y `application.properties`
> configurado. Sin modelos, repositorios, servicios ni controladores.
>
> **Objetivo:** API REST completa y lista para consumir desde el frontend Angular 20.

---

## Estructura de paquetes objetivo

```
com.inventario.backend
├── config/          — AppConfig, CorsConfig, SecurityConfig, MongoConfig
├── model/           — Entidades MongoDB (@Document)
├── dto/             — Request/Response DTOs
│   ├── request/
│   └── response/
├── repository/      — Interfaces Spring Data MongoDB
├── service/         — Lógica de negocio
│   └── impl/
├── controller/      — REST controllers
├── exception/       — Custom exceptions + GlobalExceptionHandler
├── security/        — JWT filter, UserDetailsService, helpers
└── scheduler/       — Jobs programados (@Scheduled)
```

---

## Endpoints finales por recurso

| Método | Endpoint                              | Descripción                                 | Rol mínimo        |
|--------|---------------------------------------|---------------------------------------------|-------------------|
| POST   | `/api/auth/login`                     | Login y obtención de JWT                    | público           |
| GET    | `/api/auth/me`                        | Perfil del usuario autenticado              | cualquier rol     |
| GET    | `/api/users`                          | Listar usuarios (paginado)                  | admin             |
| GET    | `/api/users/{id}`                     | Detalle de un usuario                       | admin             |
| POST   | `/api/users`                          | Crear usuario                               | admin             |
| PUT    | `/api/users/{id}`                     | Actualizar usuario                          | admin             |
| PATCH  | `/api/users/{id}/toggle-active`       | Activar / desactivar cuenta                 | admin             |
| DELETE | `/api/users/{id}`                     | Eliminar usuario                            | admin             |
| GET    | `/api/categories`                     | Listar categorías (con productCount)        | cualquier rol     |
| GET    | `/api/categories/{id}`                | Detalle de categoría                        | cualquier rol     |
| POST   | `/api/categories`                     | Crear categoría                             | admin             |
| PUT    | `/api/categories/{id}`                | Actualizar categoría                        | admin             |
| DELETE | `/api/categories/{id}`                | Eliminar categoría                          | admin             |
| GET    | `/api/products`                       | Listar productos (búsqueda, filtro, pagina) | cualquier rol     |
| GET    | `/api/products/{id}`                  | Detalle de producto                         | cualquier rol     |
| GET    | `/api/products/low-stock`             | Productos con stock ≤ minimumStock          | cualquier rol     |
| POST   | `/api/products`                       | Crear producto                              | admin/warehouse   |
| PUT    | `/api/products/{id}`                  | Actualizar producto                         | admin/warehouse   |
| DELETE | `/api/products/{id}`                  | Eliminar producto                           | admin             |
| GET    | `/api/movements`                      | Listar movimientos (filtros + paginación)   | cualquier rol     |
| GET    | `/api/movements/{id}`                 | Detalle de movimiento                       | cualquier rol     |
| GET    | `/api/movements/product/{productId}`  | Historial de un producto                    | cualquier rol     |
| POST   | `/api/movements`                      | Registrar entrada/salida (actualiza stock)  | admin/warehouse   |
| GET    | `/api/alerts`                         | Listar alertas (filtro acknowledged)        | cualquier rol     |
| PATCH  | `/api/alerts/{id}/acknowledge`        | Confirmar/revisar una alerta                | admin/warehouse   |
| POST   | `/api/alerts/generate`                | Regenerar alertas manualmente               | admin             |
| GET    | `/api/dashboard/stats`                | Estadísticas generales del dashboard        | cualquier rol     |

---

## Checklist de implementación

### FASE 0 — Dependencias adicionales y configuración base

- [ ] **0.1** Agregar dependencias en `pom.xml`:
  - `spring-boot-starter-security`
  - `io.jsonwebtoken:jjwt-api:0.12.6`
  - `io.jsonwebtoken:jjwt-impl:0.12.6` (runtime)
  - `io.jsonwebtoken:jjwt-jackson:0.12.6` (runtime)

- [ ] **0.2** Agregar en `application.properties`:
  ```properties
  jwt.secret=<clave-secreta-256-bits>
  jwt.expiration=86400000
  app.cors.allowed-origins=http://localhost:4200
  ```

- [ ] **0.3** Crear `AppConfig.java` con `@EnableMongoAuditing` para
  activar `@CreatedDate` y `@LastModifiedDate`.

- [ ] **0.4** Crear `CorsConfig.java` (`WebMvcConfigurer`) que permita
  peticiones desde `http://localhost:4200`.

- [ ] **0.5** Crear `GlobalExceptionHandler.java` (`@RestControllerAdvice`)
  con manejo de:
  - `ResourceNotFoundException` → 404
  - `DuplicateKeyException` (MongoDB) → 409
  - `MethodArgumentNotValidException` → 400 (errores de validación)
  - `Exception` genérica → 500

- [ ] **0.6** Crear excepciones custom:
  - `ResourceNotFoundException`
  - `BusinessException` (lógica de negocio, ej. stock insuficiente)
  - `UnauthorizedException`

---

### FASE 1 — Modelos (`model/`)

- [ ] **1.1** `User.java` — campos: `id`, `name`, `email`, `passwordHash`,
  `role` (enum `Role`), `isActive`, `createdAt`, `updatedAt`.

- [ ] **1.2** Enum `Role.java` — valores: `ADMIN`, `WAREHOUSE_MANAGER`, `VIEWER`.

- [ ] **1.3** `Category.java` — campos: `id`, `name`, `description`, `color`,
  `createdAt`, `updatedAt`.

- [ ] **1.4** `Product.java` — campos: `id`, `name`, `description`,
  `categoryId`, `sku`, `unit` (enum `Unit`), `currentStock`, `minimumStock`,
  `unitPrice`, `location`, `imageUrl`, `createdAt`, `updatedAt`.

- [ ] **1.5** Enum `Unit.java` — valores: `PIECE`, `BOARD`, `KG`, `LITER`,
  `METER`, `BOX`, `SHEET`.

- [ ] **1.6** `Movement.java` — campos: `id`, `productId`, `productName`,
  `type` (enum `MovementType`), `quantity`, `reason` (enum `MovementReason`),
  `notes`, `performedBy`, `performedByName`, `createdAt`.
  (**Sin** `updatedAt` — inmutable.)

- [ ] **1.7** Enums `MovementType.java` y `MovementReason.java`.

- [ ] **1.8** `StockAlert.java` — campos: `id`, `productId`, `productName`,
  `currentStock`, `minimumStock`, `severity` (enum `AlertSeverity`),
  `acknowledged`, `acknowledgedBy`, `acknowledgedAt`, `createdAt`, `updatedAt`.

- [ ] **1.9** Enum `AlertSeverity.java` — valores: `CRITICAL`, `WARNING`.

---

### FASE 2 — Repositorios (`repository/`)

- [ ] **2.1** `UserRepository extends MongoRepository<User, String>`:
  - `Optional<User> findByEmail(String email)`
  - `boolean existsByEmail(String email)`
  - `Page<User> findByRole(Role role, Pageable pageable)`

- [ ] **2.2** `CategoryRepository extends MongoRepository<Category, String>`:
  - `boolean existsByName(String name)`
  - `Optional<Category> findByName(String name)`

- [ ] **2.3** `ProductRepository extends MongoRepository<Product, String>`:
  - `boolean existsBySku(String sku)`
  - `Optional<Product> findBySku(String sku)`
  - `Page<Product> findByCategoryId(String categoryId, Pageable pageable)`
  - `List<Product> findByCurrentStockLessThanEqualAndCurrentStockGreaterThan(int min, int zero)`
  - `List<Product> findByCurrentStock(int zero)` — para críticos
  - `Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable)`

- [ ] **2.4** `MovementRepository extends MongoRepository<Movement, String>`:
  - `Page<Movement> findByProductId(String productId, Pageable pageable)`
  - `Page<Movement> findByType(MovementType type, Pageable pageable)`
  - `Page<Movement> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable)`

- [ ] **2.5** `StockAlertRepository extends MongoRepository<StockAlert, String>`:
  - `Optional<StockAlert> findByProductId(String productId)`
  - `List<StockAlert> findByAcknowledged(boolean acknowledged)`
  - `List<StockAlert> findBySeverity(AlertSeverity severity)`
  - `void deleteByProductId(String productId)`

---

### FASE 3 — DTOs (`dto/`)

#### Requests

- [ ] **3.1** `LoginRequest` — `email`, `password`
- [ ] **3.2** `CreateUserRequest` — `name`, `email`, `password`, `role`
- [ ] **3.3** `UpdateUserRequest` — `name`, `email`, `password` (opcional), `role`
- [ ] **3.4** `CreateCategoryRequest` — `name`, `description`, `color`
- [ ] **3.5** `UpdateCategoryRequest` — igual que create
- [ ] **3.6** `CreateProductRequest` — todos los campos del modelo (sin `currentStock`)
- [ ] **3.7** `UpdateProductRequest` — igual que create pero `currentStock` incluido (ajuste directo)
- [ ] **3.8** `CreateMovementRequest` — `productId`, `type`, `quantity`, `reason`, `notes`

#### Responses

- [ ] **3.9** `AuthResponse` — `token`, `expiresIn`, `user` (datos básicos sin password)
- [ ] **3.10** `UserResponse` — todos los campos excepto `passwordHash`
- [ ] **3.11** `CategoryResponse` — campos del modelo + `productCount` (calculado)
- [ ] **3.12** `ProductResponse` — todos los campos + `categoryName` (denorm. para UI)
- [ ] **3.13** `MovementResponse` — todos los campos del modelo
- [ ] **3.14** `StockAlertResponse` — todos los campos del modelo
- [ ] **3.15** `DashboardStatsResponse` — `totalProducts`, `totalCategories`,
  `lowStockCount`, `criticalStockCount`, `totalMovementsToday`,
  `totalEntriesToday`, `totalExitsToday`
- [ ] **3.16** `PagedResponse<T>` — wrapper genérico: `content`, `page`,
  `size`, `totalElements`, `totalPages`, `last`
- [ ] **3.17** `ApiResponse<T>` — wrapper estándar: `success`, `message`, `data`

---

### FASE 4 — Seguridad JWT (`security/`)

- [ ] **4.1** `JwtTokenProvider.java`:
  - `generateToken(UserDetails user)` → String JWT
  - `validateToken(String token)` → boolean
  - `getUsernameFromToken(String token)` → String (email)
  - `getExpirationFromToken(String token)` → Date

- [ ] **4.2** `JwtAuthenticationFilter.java` (`OncePerRequestFilter`):
  - Extrae Bearer token del header `Authorization`
  - Valida con `JwtTokenProvider`
  - Carga `UserDetails` y establece `SecurityContext`

- [ ] **4.3** `CustomUserDetailsService.java` (`UserDetailsService`):
  - `loadUserByUsername(String email)` — busca en `UserRepository`,
    lanza `UsernameNotFoundException` si no existe o `isActive = false`

- [ ] **4.4** `SecurityConfig.java` (`@EnableWebSecurity`):
  - Rutas públicas: `POST /api/auth/login`
  - Rutas solo `ADMIN`: `POST/PUT/DELETE /api/users/**`, `DELETE /api/categories/**`,
    `DELETE /api/products/**`, `POST /api/alerts/generate`
  - Rutas `ADMIN + WAREHOUSE_MANAGER`: `POST/PUT /api/products/**`,
    `POST /api/movements`, `PATCH /api/alerts/**`
  - Resto de GETs: cualquier usuario autenticado
  - Configurar `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`
  - Stateless session (`SessionCreationPolicy.STATELESS`)
  - `BCryptPasswordEncoder` como bean

---

### FASE 5 — Servicios (`service/impl/`)

- [ ] **5.1** `AuthService`:
  - `login(LoginRequest)` → `AuthResponse`: valida credenciales con
    `BCrypt`, genera JWT, retorna token + datos de usuario.
  - `getCurrentUser(String email)` → `UserResponse`

- [ ] **5.2** `UserService`:
  - `findAll(Pageable)` → `Page<UserResponse>`
  - `findById(String id)` → `UserResponse`
  - `create(CreateUserRequest)` → `UserResponse`:
    hashea password con BCrypt, verifica email único.
  - `update(String id, UpdateUserRequest)` → `UserResponse`
  - `toggleActive(String id)` → `UserResponse`
  - `delete(String id)` → void: impide eliminar el propio usuario autenticado.

- [ ] **5.3** `CategoryService`:
  - `findAll()` → `List<CategoryResponse>` (con `productCount` calculado via `ProductRepository.countByCategoryId`)
  - `findById(String id)` → `CategoryResponse`
  - `create(CreateCategoryRequest)` → `CategoryResponse`
  - `update(String id, UpdateCategoryRequest)` → `CategoryResponse`
  - `delete(String id)` → void: verifica que no tenga productos asociados.

- [ ] **5.4** `ProductService`:
  - `findAll(String search, String categoryId, Pageable)` → `PagedResponse<ProductResponse>`
  - `findById(String id)` → `ProductResponse`
  - `findLowStock()` → `List<ProductResponse>`
  - `create(CreateProductRequest)` → `ProductResponse`: verifica SKU único.
  - `update(String id, UpdateProductRequest)` → `ProductResponse`
  - `delete(String id)` → void

- [ ] **5.5** `MovementService`:
  - `findAll(MovementType type, LocalDateTime from, LocalDateTime to, Pageable)` → `PagedResponse<MovementResponse>`
  - `findById(String id)` → `MovementResponse`
  - `findByProduct(String productId, Pageable)` → `PagedResponse<MovementResponse>`
  - `create(CreateMovementRequest, String performedByUserId)` → `MovementResponse`:
    - Valida stock suficiente en salidas (`exit`)
    - Guarda el movement
    - Actualiza `products.currentStock` con `$inc` atómico via `MongoTemplate`
    - Dispara actualización de alerta para el producto afectado

- [ ] **5.6** `StockAlertService`:
  - `findAll(Boolean acknowledged, AlertSeverity severity)` → `List<StockAlertResponse>`
  - `acknowledge(String alertId, String userId)` → `StockAlertResponse`
  - `generateAlerts()` → `void` (upsert de alertas para todos los productos en alerta):
    - Recorre todos los productos
    - Si `currentStock == 0` → severity `CRITICAL`
    - Si `currentStock > 0 && currentStock <= minimumStock` → severity `WARNING`
    - Si stock recuperado → elimina la alerta existente
    - Usa `upsert` por `productId` (campo unique)

- [ ] **5.7** `DashboardService`:
  - `getStats()` → `DashboardStatsResponse`:
    cuenta totales desde repositorios y movimientos del día actual.

---

### FASE 6 — Controladores REST (`controller/`)

- [ ] **6.1** `AuthController` — `POST /api/auth/login`, `GET /api/auth/me`

- [ ] **6.2** `UserController` — CRUD completo + toggle-active
  - Todos los endpoints bajo `/api/users`
  - Validación con `@Valid` en requests
  - Paginación con `@PageableDefault`

- [ ] **6.3** `CategoryController` — CRUD completo bajo `/api/categories`

- [ ] **6.4** `ProductController` — CRUD + low-stock bajo `/api/products`
  - `GET /api/products?search=&categoryId=&page=&size=&sort=`

- [ ] **6.5** `MovementController` — bajo `/api/movements`
  - `GET /api/movements?type=&from=&to=&page=&size=`
  - `GET /api/movements/product/{productId}`
  - `POST /api/movements` — extrae usuario autenticado del `SecurityContext`

- [ ] **6.6** `StockAlertController` — bajo `/api/alerts`
  - `GET /api/alerts?acknowledged=&severity=`
  - `PATCH /api/alerts/{id}/acknowledge`
  - `POST /api/alerts/generate`

- [ ] **6.7** `DashboardController` — `GET /api/dashboard/stats`

---

### FASE 7 — Job programado (`scheduler/`)

- [ ] **7.1** `StockAlertScheduler.java` con `@EnableScheduling` en config:
  - `@Scheduled(fixedRate = 3600000)` → llama `StockAlertService.generateAlerts()`
    cada hora.
  - También disponible como trigger manual via `POST /api/alerts/generate`.

---

### FASE 8 — Datos iniciales (Seeder)

- [ ] **8.1** Crear `DataSeeder.java` (`ApplicationRunner`) que solo ejecute
  si la BD está vacía:
  - 1 usuario admin por defecto:
    `admin@carpentry.com` / `Admin1234!` (BCrypt)
  - 3–5 categorías de ejemplo (Lumber, Hardware, Finishes, Tools, Safety)
  - 5–10 productos de ejemplo con stock inicial
  - Ejecutar `StockAlertService.generateAlerts()` al final

---

### FASE 9 — Pruebas y verificación

- [ ] **9.1** Compilar sin errores: `./mvnw clean compile`

- [ ] **9.2** Ejecutar la aplicación: `./mvnw spring-boot:run`
  - Verificar logs de conexión a MongoDB
  - Verificar que el seeder insertó datos en la BD

- [ ] **9.3** Probar endpoints de autenticación con Postman/curl:
  - Login con credenciales del seeder → recibir JWT
  - `GET /api/auth/me` con Bearer token

- [ ] **9.4** Probar CRUD de cada recurso con roles correctos:
  - Con rol `admin`: acceso total
  - Con rol `warehouse_manager`: sin acceso a user CRUD ni deletes
  - Con rol `viewer`: solo GETs

- [ ] **9.5** Probar flujo completo de movimiento:
  - Crear un producto con stock inicial
  - Registrar una entrada → stock sube
  - Registrar una salida → stock baja
  - Forzar stock bajo → verificar alerta generada
  - Acknowledge de la alerta

- [ ] **9.6** Probar paginación y filtros en listados.

- [ ] **9.7** Verificar headers CORS desde `localhost:4200`:
  - Hacer petición desde el frontend Angular y confirmar sin errores de CORS.

- [ ] **9.8** Ejecutar tests unitarios: `./mvnw test`

---

## Orden de implementación recomendado

```
0 → 1 → 2 → 3 → 4 → 5.1 → 6.1 → (probar auth)
→ 5.3 → 6.3 → 5.4 → 6.4 → 5.2 → 6.2
→ 5.5 → 6.5 → 5.6 → 7.1 → 6.6
→ 5.7 → 6.7 → 8 → 9
```

---

## Notas técnicas importantes

- **Atomicidad en movimientos:** Usar `MongoTemplate.updateFirst()` con
  `Update.update(...).inc("currentStock", delta)` para garantizar actualización
  atómica del stock sin condiciones de carrera.

- **productCount en categorías:** Calcular con
  `mongoTemplate.count(query, Product.class)` en `CategoryService`, no persistir
  el campo.

- **Stock insuficiente:** En `MovementService.create()` para `type = EXIT`,
  validar que `product.currentStock >= quantity` antes de guardar. Si no, lanzar
  `BusinessException(400, "Stock insuficiente")`.

- **Inmutabilidad de movements:** Los documentos de `movements` **nunca** se
  actualizan ni eliminan. No exponer endpoints de PUT/DELETE para esta colección.

- **JWT en headers:** El frontend Angular enviará `Authorization: Bearer <token>`
  en cada petición. El `JwtAuthenticationFilter` lo debe interceptar antes del
  controlador.

- **Campos denormalizados:** Al crear un `Movement`, resolver `productName` y
  `performedByName` en el servicio antes de persistir, no confiar en que el
  cliente los envíe.

- **Upsert de alertas:** La colección `stock_alerts` tiene índice único por
  `productId`. Usar `mongoTemplate.upsert()` con `Query` por `productId` para
  actualizar-o-crear en un solo paso.