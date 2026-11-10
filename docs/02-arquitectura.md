# 02 · Arquitectura

El proyecto evoluciona en dos etapas:

| Etapa | Estilo | Propósito |
|---|---|---|
| **Actual** (v0.x) | **MVC en capas** con Spring Boot | Entrega académica: CRUD de dos entidades relacionadas con MySQL |
| **Objetivo** (v1.x+) | **Hexagonal (puertos y adaptadores) + eventos** | Cumplir tiempo real, mantenibilidad de reglas y disponibilidad |

Las razones de cada decisión están en [adr/](adr/).

---

## 1. Arquitectura actual: MVC

### 1.1 Estructura de carpetas

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
    │       └── templates/                    # VISTA: plantillas Thymeleaf
    │           ├── index.html
    │           ├── fragments/layout.html
    │           ├── pacientes/ (lista, formulario, detalle)
    │           └── medicos/   (lista, formulario, detalle)
    └── test/java/com/pascualbravo/urgencias/
        └── UrgenciasApplicationTests.java
```

### 1.2 Correspondencia MVC

| Componente MVC | Paquete / carpeta | Responsabilidad |
|---|---|---|
| **Modelo** | `model` | Entidades `Paciente` y `Medico`, enum `EstadoPaciente`, validaciones con Bean Validation |
| **Modelo** | `repository` | Interfaces Spring Data JPA: persistencia y consultas (`findAllByOrderByNivelGravedadDescFechaLlegadaAsc`) |
| **Modelo** | `service` | Reglas de negocio: cálculo de prioridad, fecha de llegada, validación de eliminación |
| **Vista** | `resources/templates` | Formularios y tablas HTML con Thymeleaf; layout compartido en `fragments/` |
| **Controlador** | `controller` | Recibe peticiones, valida (`@Valid`), llama al servicio y elige la vista |

### 1.3 Modelo de datos

```
┌──────────────────────┐ 1        * ┌───────────────────────────┐
│ medicos              │───────────│ pacientes                  │
├──────────────────────┤            ├───────────────────────────┤
│ id (PK)              │            │ id (PK)                    │
│ nombre               │            │ nombre                     │
│ especialidad         │            │ documento (UNIQUE)         │
│ correo               │            │ edad (0–120)               │
│ disponible           │            │ motivo_consulta            │
└──────────────────────┘            │ nivel_gravedad (1–5)       │
                                    │ alergias                   │
                                    │ prioridad (calculada)      │
                                    │ fecha_llegada              │
                                    │ estado (EN_ESPERA,         │
                                    │   EN_ATENCION, ATENDIDO)   │
                                    │ medico_id (FK, NOT NULL)   │
                                    └───────────────────────────┘
```

Relación: **un médico atiende muchos pacientes** (`@OneToMany` en `Medico`, `@ManyToOne` en `Paciente`). No se permite eliminar un médico con pacientes asociados.

### 1.4 Flujo de una petición

```
Navegador ──HTTP──▶ PacienteController ──▶ PacienteService ──▶ PacienteRepository ──▶ MySQL
    ▲                       │                     │
    └──── HTML (Thymeleaf) ◀┘          calcularPrioridad()
```

Ejemplo — registrar paciente (`POST /pacientes/guardar`):
1. El controlador valida el formulario con `@Valid`; si hay errores, devuelve el formulario con mensajes.
2. `PacienteService.guardar()` asigna la fecha de llegada y calcula la prioridad.
3. `PacienteRepository.save()` persiste en la tabla `pacientes`.
4. Redirección a `/pacientes`, donde la lista se ordena por gravedad y hora de llegada.

### 1.5 Reglas de dependencia (MVC)

- `controller` → `service` → `repository` → `model`. Nunca al revés.
- Los controladores **no** acceden a repositorios directamente.
- Las plantillas **no** contienen lógica de negocio (solo presentación).
- La lógica de prioridad vive **solo** en la capa de servicio (y, en la etapa objetivo, en el dominio).

---

## 2. Arquitectura objetivo: hexagonal con eventos

Decisión del taller *Estilos Arquitectónicos*: **arquitectura hexagonal con comunicación basada en eventos**, desplegada en varias instancias sin estado. Los microservicios quedan como evolución planificada, no como punto de partida.

### 2.1 Estructura propuesta

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

### 2.2 Tecnologías previstas

Spring Web, Spring Data JPA, Spring WebSocket (STOMP), Spring Security (JWT), Spring AMQP (RabbitMQ), PostgreSQL, Flyway, ArchUnit, Testcontainers, Docker.

### 2.3 Cómo responde a los atributos de calidad

| Atributo | Mecanismo |
|---|---|
| Disponibilidad | Instancias sin estado detrás de balanceador; broker desacopla productores y consumidores |
| Seguridad | Spring Security + JWT, RBAC por rol, TLS, cifrado de datos sensibles |
| Rendimiento / concurrencia | Eventos + WebSocket para empujar la cola; recálculo con funciones puras |
| Mantenibilidad | Reglas como estrategias en el dominio; ArchUnit vigila la regla de dependencia |
| Escalabilidad | Escalado horizontal; camino de evolución a microservicios |

### 2.4 Migración MVC → hexagonal

La migración se hará de forma incremental en ramas `feature/*` (ver [07-roadmap.md](07-roadmap.md)), comenzando por extraer el cálculo de prioridad a `domain/rules` con pruebas unitarias, de modo que la aplicación siga funcionando en cada paso.
