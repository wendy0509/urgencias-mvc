# 02 · Arquitectura

El proyecto evoluciona en tres etapas:

| Etapa | Estilo | Propósito |
|---|---|---|
| Anterior (v0.1) | **MVC en capas** con Spring Boot | Actividad *Aplicación web con MVC*: CRUD de dos entidades relacionadas con MySQL |
| **Actual** (v0.2) | **Hexagonal (puertos y adaptadores)** | Actividad *Transformación de MVC a Hexagonal*: mismo CRUD con el negocio aislado de la tecnología |
| Objetivo (v1.x+) | **Hexagonal + eventos** | Tiempo real, mantenibilidad de reglas y disponibilidad |

Las razones de cada decisión están en [adr/](adr/). El análisis completo de la transformación (tabla comparativa, diagramas y reflexión) está en [10-documento-tecnico-hexagonal.md](10-documento-tecnico-hexagonal.md).

---

## 1. Arquitectura actual: hexagonal

### 1.1 Estructura de paquetes

```
src/main/java/com/pascualbravo/urgencias/
├── UrgenciasApplication.java
├── domain/                       # NÚCLEO — Java puro, sin Spring ni JPA
│   ├── model/                    # Paciente, Medico, Prioridad, EstadoPaciente
│   ├── exception/                # ReglaNegocio, DocumentoDuplicado, RecursoNoEncontrado
│   └── port/
│       ├── in/                   # Casos de uso: GestionarPacienteUseCase, GestionarMedicoUseCase
│       └── out/                  # PacienteRepositoryPort, MedicoRepositoryPort
├── application/
│   └── service/                  # PacienteService, MedicoService (implementan port/in, usan port/out)
└── infrastructure/
    ├── adapter/in/web/           # Controladores, dto/ (formularios), mapper/, WebExceptionHandler
    ├── adapter/out/persistence/  # Adaptadores JPA, entity/, repository/ (Spring Data), mapper/
    └── config/                   # BeanConfiguration: ensambla servicios + adaptadores

src/main/resources/templates/     # Vistas Thymeleaf (sin cambios de diseño)
src/main/resources/static/js/     # Validación en el navegador
```

### 1.2 Regla de dependencia

```
adapter.in.web ──▶ domain.port.in ◀── application.service ──▶ domain.port.out ◀── adapter.out.persistence
                                              │
                                              ▼
                                        domain.model
```

- Todas las dependencias apuntan **hacia el dominio**.
- `domain` no importa Spring, JPA, Jakarta ni otras capas.
- `application` no importa `infrastructure` ni Spring (los servicios se registran como beans en `infrastructure/config`).
- Los controladores usan **solo puertos de entrada**; nunca repositorios, entidades JPA ni servicios concretos.
- Estas reglas se verifican automáticamente en `ArquitecturaHexagonalTest` (ArchUnit) al ejecutar `./mvnw verify`.

### 1.3 Tres representaciones de cada entidad

| Representación | Paquete | Sirve para |
|---|---|---|
| `Paciente`, `Medico` | `domain.model` | Reglas de negocio (prioridad, rangos, médico obligatorio) |
| `PacienteForm`, `MedicoForm` | `adapter.in.web.dto` | Formularios + Bean Validation (mensajes de [09](09-reglas-de-campos.md)) |
| `PacienteEntity`, `MedicoEntity` | `adapter.out.persistence.entity` | Mapeo JPA a las tablas `pacientes` y `medicos` |

Los *mappers* de cada adaptador traducen entre ellas.

### 1.4 Modelo de datos

Sin cambios respecto a MVC (ver 2.3): `medicos (1) ── (*) pacientes` mediante `pacientes.medico_id`. Una base de datos creada con la versión MVC funciona sin migraciones.

### 1.5 Flujo de una petición — registrar paciente (`POST /pacientes/guardar`)

1. `validacion-formularios.js` valida en el navegador.
2. `PacienteController` valida el formato con `@Valid PacienteForm` y llama a `GestionarPacienteUseCase.registrar(paciente, medicoId)`.
3. `PacienteService` verifica documento único y existencia del médico mediante los puertos de salida, y pide al dominio `asignarMedico()`, `registrarLlegada()` y `clasificar()`.
4. `Paciente.clasificar()` valida rangos y calcula la prioridad con `Prioridad.calcular()`.
5. `PacienteRepositoryPort.guardar()` → `PacientePersistenceAdapter` → Spring Data JPA → MySQL.
6. Si hay `DocumentoDuplicadoException`, el controlador muestra el error bajo el campo; si no, redirige a `/pacientes`.

---

## 2. Arquitectura anterior: MVC (v0.1)

Se conserva como referencia para la comparación con la versión hexagonal.


### 2.1 Estructura de carpetas

```
urgencias-mvc/
├── pom.xml                         # Dependencias y build (Maven)
├── mvnw / mvnw.cmd                 # Maven Wrapper
├── docs/                           # Documentación del proyecto
└── src/
    ├── main/
    │   ├── java/com/pascualbravo/urgencias/
    │   │   ├── UrgenciasApplication.java     # Punto de entrada Spring Boot
    │   │   ├── model/                        # MODELO: entidades JPA y enums
    │   │   │   ├── Paciente.java
    │   │   │   ├── Medico.java
    │   │   │   └── EstadoPaciente.java
    │   │   ├── repository/                   # MODELO (acceso a datos): Spring Data JPA
    │   │   │   ├── PacienteRepository.java
    │   │   │   └── MedicoRepository.java
    │   │   ├── service/                      # MODELO (lógica de negocio)
    │   │   │   ├── PacienteService.java
    │   │   │   └── MedicoService.java
    │   │   └── controller/                   # CONTROLADOR: rutas HTTP
    │   │       ├── HomeController.java
    │   │       ├── PacienteController.java
    │   │       └── MedicoController.java
    │   └── resources/
    │       ├── application.properties        # Conexión a BD y configuración
    │       ├── messages.properties           # Mensajes en español de errores de conversión
    │       ├── static/js/
    │       │   └── validacion-formularios.js # VISTA: validación en tiempo real (navegador)
    │       └── templates/                    # VISTA: plantillas Thymeleaf
    │           ├── index.html
    │           ├── fragments/layout.html
    │           ├── pacientes/ (lista, formulario, detalle)
    │           └── medicos/   (lista, formulario, detalle)
    └── test/java/com/pascualbravo/urgencias/
        └── UrgenciasApplicationTests.java
```

### 2.2 Correspondencia MVC

| Componente MVC | Paquete / carpeta | Responsabilidad |
|---|---|---|
| **Modelo** | `model` | Entidades `Paciente` y `Medico`, enum `EstadoPaciente`, validaciones con Bean Validation |
| **Modelo** | `repository` | Interfaces Spring Data JPA: persistencia y consultas (`findAllByOrderByNivelGravedadDescFechaLlegadaAsc`) |
| **Modelo** | `service` | Reglas de negocio: cálculo de prioridad, fecha de llegada, validación de eliminación |
| **Vista** | `resources/templates` | Formularios y tablas HTML con Thymeleaf; layout compartido en `fragments/` |
| **Vista** | `resources/static/js` | Validación de formularios en el navegador (no contiene reglas de negocio) |
| **Controlador** | `controller` | Recibe peticiones, valida (`@Valid`), llama al servicio y elige la vista |

### 2.3 Modelo de datos

```
┌──────────────────────┐ 1        * ┌───────────────────────────┐
│ medicos              │───────────│ pacientes                  │
├──────────────────────┤            ├───────────────────────────┤
│ id (PK)              │            │ id (PK)                    │
│ nombre (100)         │            │ nombre (100)               │
│ especialidad (80)    │            │ documento (5–20 dígitos,   │
│ correo (100)         │            │   UNIQUE)                  │
│ disponible           │            │ edad (0–120)               │
└──────────────────────┘            │ motivo_consulta (255)      │
                                    │ nivel_gravedad (1–5)       │
                                    │ alergias (255)             │
                                    │ prioridad (calculada)      │
                                    │ fecha_llegada              │
                                    │ estado (EN_ESPERA,         │
                                    │   EN_ATENCION, ATENDIDO)   │
                                    │ medico_id (FK, NOT NULL)   │
                                    └───────────────────────────┘
```

Relación: **un médico atiende muchos pacientes** (`@OneToMany` en `Medico`, `@ManyToOne` en `Paciente`). No se permite eliminar un médico con pacientes asociados.

### 2.4 Flujo de una petición

```
Navegador ──HTTP──▶ PacienteController ──▶ PacienteService ──▶ PacienteRepository ──▶ MySQL
    ▲                       │                     │
    └──── HTML (Thymeleaf) ◀┘          calcularPrioridad()
```

Ejemplo — registrar paciente (`POST /pacientes/guardar`):
0. En el navegador, `validacion-formularios.js` impide escribir datos inválidos y no deja enviar el formulario mientras haya errores.
1. El controlador valida el formulario con `@Valid` (respaldo); si hay errores, devuelve el formulario con mensajes.
2. `PacienteService.guardar()` asigna la fecha de llegada y calcula la prioridad.
3. `PacienteRepository.save()` persiste en la tabla `pacientes`.
4. Redirección a `/pacientes`, donde la lista se ordena por gravedad y hora de llegada.

### 2.5 Reglas de dependencia (MVC)

- `controller` → `service` → `repository` → `model`. Nunca al revés.
- Los controladores **no** acceden a repositorios directamente.
- Las plantillas **no** contienen lógica de negocio (solo presentación).
- La lógica de prioridad vive **solo** en la capa de servicio (y, en la etapa objetivo, en el dominio).
- Las reglas de los campos se definen en la entidad **y** en la plantilla con los mismos mensajes; el JavaScript del navegador mejora la experiencia pero nunca reemplaza la validación del servidor. Ver [09-reglas-de-campos.md](09-reglas-de-campos.md).

---

## 3. Arquitectura objetivo: hexagonal con eventos

Decisión del taller *Estilos Arquitectónicos*: **arquitectura hexagonal con comunicación basada en eventos**, desplegada en varias instancias sin estado. Los microservicios quedan como evolución planificada, no como punto de partida.

### 3.1 Estructura propuesta

```
src/main/java/co/edu/pascualbravo/triage/
├── domain/                 # Núcleo: sin dependencias de Spring
│   ├── model/              # Paciente, SignosVitales, Prioridad, HistorialAtencion
│   ├── rules/              # ReglaDePrioridad + ReglaEdad, ReglaGravedad, ReglaSignosVitales, ReglaTiempoEspera
│   ├── event/              # PacienteRegistrado, PrioridadRecalculada, AtencionFinalizada
│   └── exception/
├── application/
│   ├── port/in/            # Casos de uso: RegistrarPaciente, RecalcularPrioridad, ...
│   ├── port/out/           # RepositorioPacientes, RepositorioHistorial, PublicadorEventos, NotificadorCola
│   └── service/            # Implementación de los casos de uso
└── infrastructure/
    ├── adapter/in/web/        # Controladores REST
    ├── adapter/in/scheduler/  # Revisión periódica de tiempos de espera (RF03)
    ├── adapter/out/persistence/ # JPA
    ├── adapter/out/messaging/   # RabbitMQ
    ├── adapter/out/websocket/   # Cola en tiempo real (STOMP)
    ├── adapter/out/report/
    └── config/security/       # Spring Security + JWT
```

### 3.2 Tecnologías previstas

Spring Web, Spring Data JPA, Spring WebSocket (STOMP), Spring Security (JWT), Spring AMQP (RabbitMQ), PostgreSQL, Flyway, ArchUnit, Testcontainers, Docker.

### 3.3 Cómo responde a los atributos de calidad

| Atributo | Mecanismo |
|---|---|
| Disponibilidad | Instancias sin estado detrás de balanceador; broker desacopla productores y consumidores |
| Seguridad | Spring Security + JWT, RBAC por rol, TLS, cifrado de datos sensibles |
| Rendimiento / concurrencia | Eventos + WebSocket para empujar la cola; recálculo con funciones puras |
| Mantenibilidad | Reglas como estrategias en el dominio; ArchUnit vigila la regla de dependencia |
| Escalabilidad | Escalado horizontal; camino de evolución a microservicios |

### 3.4 De la arquitectura actual a la objetivo

La migración de MVC a hexagonal ya se realizó ([ADR-0004](adr/0004-migracion-a-hexagonal.md)). Lo que falta es incremental sobre la estructura actual: reglas de prioridad como estrategias en `domain`, eventos y WebSocket como nuevos adaptadores, y seguridad en `infrastructure/config` (ver [07-roadmap.md](07-roadmap.md)). Nota: por indicación de la guía de la actividad, los puertos quedaron en `domain/port/{in,out}` en lugar de `application/port`.
