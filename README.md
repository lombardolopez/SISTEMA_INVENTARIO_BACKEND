# Sistema de Inventario — Backend

REST API para el sistema de gestión de inventario de carpintería, construida con Spring Boot 3.4.3 y MongoDB.

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 23 |
| Spring Boot | 3.4.3 |
| Spring Data MongoDB | (incluido en Boot) |
| Spring Validation | (incluido en Boot) |
| Lombok | (incluido en Boot) |
| Maven | 3.9+ |

## Requisitos previos

- **Java 23** instalado y configurado en `JAVA_HOME`
- **Maven 3.9+** (el proyecto incluye Maven Wrapper `mvnw`)
- **MongoDB** corriendo en `localhost:27017`

> **Nota:** Maven también puede estar disponible vía Chocolatey en:
> `/c/ProgramData/chocolatey/lib/maven/apache-maven-3.9.12/bin`

## Configuración

El archivo de configuración principal se encuentra en:

```
src/main/resources/application.properties
```

```properties
spring.application.name=sistema-inventario-backend
server.port=8080

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/inventario
```

La base de datos utilizada es `inventario` en MongoDB local. Si necesitas cambiar la URI de conexión, edita la propiedad `spring.data.mongodb.uri`.

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
./mvnw clean package

# Generar el JAR sin ejecutar tests
./mvnw clean package -DskipTests
```

El JAR generado se encuentra en `target/backend-0.0.1-SNAPSHOT.jar` y puede ejecutarse con:

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Estructura del proyecto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/inventario/backend/
│   │   │   └── BackendApplication.java      # Punto de entrada
│   │   └── resources/
│   │       └── application.properties       # Configuración
│   └── test/
│       └── java/com/inventario/backend/
│           └── BackendApplicationTests.java
├── .mvn/wrapper/
│   └── maven-wrapper.properties
├── mvnw                                      # Maven Wrapper (Unix)
├── mvnw.cmd                                  # Maven Wrapper (Windows)
├── pom.xml                                   # Dependencias y build
└── README.md
```

## Paquete base

```
com.inventario.backend
```

## Contexto del monorepo

Este backend forma parte de un monorepo junto con el frontend Angular 20. La estructura completa del proyecto es:

```
Sistema de Inventario/
├── backend/    ← estás aquí
└── frontend/
```

El frontend consume esta API desde `http://localhost:8080`.