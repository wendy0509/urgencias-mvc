# 03 · Stack tecnológico

## 1. Lenguajes

| Lenguaje | Uso |
|---|---|
| **Java 21 (LTS)** | Backend: modelo, servicios, controladores |
| **HTML5 + Thymeleaf** | Vistas del lado del servidor |
| **CSS3 (Bootstrap 5)** | Estilos de la interfaz |
| **JavaScript (ES5, sin librerías)** | Validación de formularios en tiempo real (`static/js/validacion-formularios.js`); en la etapa objetivo, cliente WebSocket |
| **SQL (MySQL)** | Base de datos relacional |
| **Markdown** | Documentación en `docs/` |

## 2. Frameworks y librerías (versión actual)

| Herramienta | Versión | Uso |
|---|---|---|
| Spring Boot | 4.1.2 | Framework base |
| spring-boot-starter-webmvc | (Boot) | Controladores MVC |
| spring-boot-starter-thymeleaf | (Boot) | Motor de plantillas |
| spring-boot-starter-data-jpa | (Boot) | Persistencia con Hibernate |
| spring-boot-starter-validation | (Boot) | `@NotBlank`, `@Size`, `@Pattern`, `@Min`, `@Max`, `@Email` — ver [09-reglas-de-campos.md](09-reglas-de-campos.md) |
| mysql-connector-j | (Boot) | Driver MySQL |
| Lombok | (Boot) | `@Getter`, `@Setter`, `@NoArgsConstructor` |
| Spring DevTools | (Boot) | Recarga en caliente en desarrollo |
| JUnit 5 + Spring Boot Test | (Boot) | Pruebas |

> Las versiones marcadas como *(Boot)* las administra el `spring-boot-starter-parent`. No se fijan versiones manualmente salvo necesidad justificada en el PR.

## 3. Herramientas de desarrollo

| Herramienta | Versión mínima | Notas |
|---|---|---|
| JDK | 21 | Temurin u Oracle |
| Maven | Wrapper incluido (`mvnw`) | No es necesario instalar Maven |
| XAMPP (MySQL/MariaDB) | 8.2.12 recomendado | Puerto configurado en el proyecto: **3307** (XAMPP trae 3306 por defecto; ver manual 3.1) |
| IDE | IntelliJ IDEA, VS Code (Extension Pack for Java + Spring Boot) o STS | Activar *annotation processing* para Lombok |
| Git | 2.40+ | Ver flujo en [04-flujo-de-trabajo-git.md](04-flujo-de-trabajo-git.md). Windows: `winget install --id Git.Git -e` |
| Navegador | Chrome / Edge / Firefox actualizados | |

## 4. Tecnologías previstas para la arquitectura objetivo

Se incorporarán progresivamente, cada una en su propia rama `feature/*` y con su ADR si cambia una decisión:

| Tecnología | Requisito que atiende |
|---|---|
| Spring Security + JWT | RF10, RNF04 |
| Spring WebSocket (STOMP) | RF04 |
| RabbitMQ (Spring AMQP) | RF03, RNF03 |
| Flyway (migraciones) | RNF08, despliegues repetibles |
| ArchUnit | RNF06 — reglas de dependencia |
| Testcontainers | Pruebas de adaptadores |
| Docker / docker-compose | RNF01, RNF05 |
| PostgreSQL | Posible reemplazo de MySQL en producción (pendiente de ADR) |
