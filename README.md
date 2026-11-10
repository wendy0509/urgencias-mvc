# Sistema de Priorización de Atención en Urgencias Médicas

Aplicación web para registrar pacientes en el servicio de urgencias, calcular su prioridad de atención con criterios objetivos (edad, gravedad, signos vitales y tiempo de espera) y mostrar una cola de atención ordenada. El sistema **apoya, no reemplaza**, el criterio clínico del personal médico.

Proyecto de aula de la materia **Arquitectura de Software** — Institución Universitaria Pascual Bravo, Medellín, 2026.

| Integrantes |
|---|
| Samuel López Marín |
| Wendy Stefania Infante Vera |
| Karoll Dayanna Moreno Mahecha |

---

## Estado actual

Versión en desarrollo **0.1.0-SNAPSHOT**: aplicación **Spring Boot MVC** con CRUD de `Paciente` y `Medico` (relación muchos a uno), validaciones y cálculo simplificado de prioridad. Corresponde a la actividad *Desarrollo de una aplicación web utilizando Arquitectura MVC con Spring Boot*.

La arquitectura objetivo a largo plazo (hexagonal con eventos) está descrita en [docs/02-arquitectura.md](docs/02-arquitectura.md).

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1.2 (Spring Web MVC, Spring Data JPA, Bean Validation) |
| Vistas | Thymeleaf + HTML5 / CSS (Bootstrap) |
| Base de datos | MySQL (XAMPP), puerto 3307, base `urgencias_db` |
| Build | Maven (wrapper `mvnw` incluido) |
| Utilidades | Lombok, Spring DevTools |

Detalle completo en [docs/03-stack-tecnologico.md](docs/03-stack-tecnologico.md).

## Inicio rápido

```bash
# 1. Iniciar MySQL en XAMPP (puerto 3307) y crear la base de datos
#    CREATE DATABASE urgencias_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. Clonar y entrar a la rama de desarrollo
git clone https://github.com/wendy0509/urgencias-mvc.git
cd urgencias-mvc
git checkout develop

# 3. Ejecutar
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

Abrir <http://localhost:8080/>. Las tablas se crean solas (`ddl-auto=update`).

Manual completo (requisitos, configuración de BD, URLs y guía de uso): [docs/06-manual-del-sistema.md](docs/06-manual-del-sistema.md).

## Rutas principales

| Funcionalidad | URL |
|---|---|
| Página principal | `http://localhost:8080/` |
| Listar pacientes (cola por prioridad) | `http://localhost:8080/pacientes` |
| Registrar paciente | `http://localhost:8080/pacientes/nuevo` |
| Listar médicos | `http://localhost:8080/medicos` |
| Registrar médico | `http://localhost:8080/medicos/nuevo` |

## Documentación

| Documento | Contenido |
|---|---|
| [01 · Visión y alcance](docs/01-vision-y-alcance.md) | Problema, usuarios, alcance, requisitos RF/RNF, atributos de calidad |
| [02 · Arquitectura](docs/02-arquitectura.md) | Estructura MVC actual, flujo de una petición, arquitectura objetivo |
| [03 · Stack tecnológico](docs/03-stack-tecnologico.md) | Lenguajes, frameworks, versiones y herramientas |
| [04 · Flujo de trabajo Git](docs/04-flujo-de-trabajo-git.md) | Ramas `main`, `develop`, `feature/*`, `release/*`, `hotfix/*`; Pull Requests |
| [05 · Convenciones de código](docs/05-convenciones-de-codigo.md) | Nombres, paquetes, commits, estilo |
| [06 · Manual del sistema](docs/06-manual-del-sistema.md) | Instalación, configuración de BD, URLs, guía de uso |
| [07 · Roadmap](docs/07-roadmap.md) | Fases, mapeo de requisitos a ramas, deuda técnica conocida |
| [ADR](docs/adr/) | Registro de decisiones de arquitectura |
| [CONTRIBUTING](CONTRIBUTING.md) | Cómo contribuir paso a paso |
| [CHANGELOG](CHANGELOG.md) | Historial de versiones |

## Ramas

```
main      ← solo versiones desplegadas / entregadas (protegida)
develop   ← integración del trabajo diario (rama por defecto de trabajo)
feature/* ← una funcionalidad o documento, sale de develop y vuelve a develop
release/* ← preparación de una entrega, sale de develop y va a main + develop
hotfix/*  ← corrección urgente sobre main
```

Todo cambio entra por **Pull Request revisado y aprobado**. Ver [docs/04-flujo-de-trabajo-git.md](docs/04-flujo-de-trabajo-git.md).
