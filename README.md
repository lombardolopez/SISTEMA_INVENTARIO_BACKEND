# Sistema de Inventario — Backend

REST API para el sistema de gestión de inventario de carpintería, construida con Spring Boot 3.4.3, Spring Security (JWT) y MongoDB.

## Tecnologías

| Tecnología          | Versión              |
|---------------------|----------------------|
| Java                | 23                   |
| Spring Boot         | 3.4.3                |
| Spring Security     | (incluido en Boot)   |
| Spring Data MongoDB | (incluido en Boot)   |
| Spring Validation   | (incluido en Boot)   |
| JJWT                | 0.12.6               |
| Lombok              | 1.18.36              |
| Maven               | 3.9+                 |
| MongoDB             | 7.x                  |

## Requisitos previos

- **Java 23** instalado y configurado en `JAVA_HOME`
- **Maven 3.9+** (el proyecto incluye Maven Wrapper `mvnw`)
- **MongoDB** corriendo en `localhost:27017` (sin autenticación)

## Configuración

El archivo de configuración principal se encuentra en:

```
src/main/resources/application.properties
```

```properties
server.port=8080
spring.data.mongodb.uri=mongodb://localhost:27017/inventario
jwt.expiration=86400000   # 24 horas en ms
```

Si necesitas cambiar la URI de conexión o el puerto, edita ese archivo directamente.

## Comandos

Ejecutar todos los comandos desde la carpeta `backend/`.

```bash
# Levantar la aplicación en modo desarrollo (localhost:8080)
./mvnw spring-boot:run

# Compilar el proyecto
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Generar el JAR ejecutable
./mvnw clean package -DskipTests

# Formatear el código (Google Java Format)
./mvnw spotless:apply
```

El JAR generado se encuentra en `target/backend-0.0.1-SNAPSHOT.jar` y puede ejecutarse con:

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Datos iniciales (Seeder)

Al arrancar por primera vez, el sistema inserta automáticamente datos de ejemplo en MongoDB:

| Recurso     | Cantidad |
|-------------|----------|
| Usuarios    | 3        |
| Categorías  | 5        |
| Productos   | 10       |
| Alertas     | 5        |

### Credenciales por defecto

| Email                    | Contraseña       | Rol               |
|--------------------------|------------------|-------------------|
| admin@carpentry.com      | Admin1234!       | ADMIN             |
| carlos@carpentry.com     | Warehouse1234!   | WAREHOUSE_MANAGER |
| ana@carpentry.com        | Viewer1234!      | VIEWER            |

> El seeder solo se ejecuta si la base de datos está vacía. En ejecuciones posteriores se omite automáticamente.

## Seguridad

La API utiliza autenticación basada en **JWT (JSON Web Token)**:

- El token se obtiene en `POST /api/auth/login`
- Se envía en cada request como header: `Authorization: Bearer <token>`
- Expiración: **24 horas**

### Roles y permisos

| Rol               | Permisos                                                        |
|-------------------|-----------------------------------------------------------------|
| ADMIN             | Acceso total: usuarios, categorías, productos, movimientos      |
| WAREHOUSE_MANAGER | Crear/editar productos, registrar movimientos, gestionar alertas|
| VIEWER            | Solo lectura en todos los recursos                              |

## Endpoints

### Autenticación
| Método | Ruta            | Descripción                  | Acceso    |
|--------|-----------------|------------------------------|-----------|
| POST   | /api/auth/login | Iniciar sesión, obtener JWT  | Público   |
| GET    | /api/auth/me    | Datos del usuario autenticado| Todos     |

### Usuarios
| Método | Ruta                          | Descripción              | Acceso |
|--------|-------------------------------|--------------------------|--------|
| GET    | /api/users                    | Listar usuarios          | ADMIN  |
| GET    | /api/users/{id}               | Obtener usuario          | ADMIN  |
| POST   | /api/users                    | Crear usuario            | ADMIN  |
| PUT    | /api/users/{id}               | Actualizar usuario       | ADMIN  |
| PATCH  | /api/users/{id}/toggle-active | Activar/desactivar       | ADMIN  |
| DELETE | /api/users/{id}               | Eliminar usuario         | ADMIN  |

### Categorías
| Método | Ruta                  | Descripción         | Acceso             |
|--------|-----------------------|---------------------|--------------------|
| GET    | /api/categories       | Listar categorías   | Todos              |
| GET    | /api/categories/{id}  | Obtener categoría   | Todos              |
| POST   | /api/categories       | Crear categoría     | ADMIN              |
| PUT    | /api/categories/{id}  | Actualizar categoría| ADMIN              |
| DELETE | /api/categories/{id}  | Eliminar categoría  | ADMIN              |

### Productos
| Método | Ruta                     | Descripción                       | Acceso              |
|--------|--------------------------|-----------------------------------|---------------------|
| GET    | /api/products            | Listar (search, categoryId, page) | Todos               |
| GET    | /api/products/{id}       | Obtener producto                  | Todos               |
| GET    | /api/products/low-stock  | Productos con stock bajo          | Todos               |
| POST   | /api/products            | Crear producto                    | ADMIN, WAREHOUSE    |
| PUT    | /api/products/{id}       | Actualizar producto               | ADMIN, WAREHOUSE    |
| DELETE | /api/products/{id}       | Eliminar producto                 | ADMIN               |

### Movimientos
| Método | Ruta                              | Descripción                        | Acceso           |
|--------|-----------------------------------|------------------------------------|------------------|
| GET    | /api/movements                    | Listar (type, from, to, page)      | Todos            |
| GET    | /api/movements/{id}               | Obtener movimiento                 | Todos            |
| GET    | /api/movements/product/{productId}| Movimientos de un producto         | Todos            |
| POST   | /api/movements                    | Registrar movimiento (ENTRY/EXIT)  | ADMIN, WAREHOUSE |

### Alertas de stock
| Método | Ruta                      | Descripción                     | Acceso           |
|--------|---------------------------|---------------------------------|------------------|
| GET    | /api/alerts               | Listar alertas (acknowledged, severity) | Todos    |
| PATCH  | /api/alerts/{id}/acknowledge | Marcar como revisada         | ADMIN, WAREHOUSE |
| POST   | /api/alerts/generate      | Forzar generación de alertas    | ADMIN            |

### Dashboard
| Método | Ruta                  | Descripción                  | Acceso |
|--------|-----------------------|------------------------------|--------|
| GET    | /api/dashboard/stats  | Estadísticas generales       | Todos  |

## Estructura del proyecto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/inventario/backend/
│   │   │   ├── BackendApplication.java
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java          # @EnableMongoAuditing, @EnableScheduling
│   │   │   │   ├── CorsConfig.java         # CORS para MVC
│   │   │   │   ├── SecurityConfig.java     # Spring Security + JWT filter chain
│   │   │   │   └── DataSeeder.java         # Datos iniciales en primer arranque
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── MovementController.java
│   │   │   │   ├── StockAlertController.java
│   │   │   │   └── DashboardController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/               # LoginRequest, CreateProductRequest, etc.
│   │   │   │   └── response/              # ApiResponse, PagedResponse, ProductResponse, etc.
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── BusinessException.java
│   │   │   ├── model/                     # User, Product, Category, Movement, StockAlert + enums
│   │   │   ├── repository/                # MongoRepository por entidad
│   │   │   ├── scheduler/
│   │   │   │   └── StockAlertScheduler.java  # Generación automática cada hora
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   └── service/
│   │   │       ├── impl/                  # AuthServiceImpl, ProductServiceImpl, etc.
│   │   │       └── *.java                 # Interfaces de servicio
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/inventario/backend/
│           └── BackendApplicationTests.java
├── pom.xml
└── README.md
```

## Contexto del monorepo

Este backend forma parte de un monorepo junto con el frontend Angular 20:

```
Sistema de Inventario/
├── backend/    ← estás aquí  (Spring Boot · puerto 8080)
└── frontend/               (Angular 20  · puerto 4200)
```

El frontend consume esta API desde `http://localhost:8080`.