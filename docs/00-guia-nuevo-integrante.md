# 00 · Guía para nuevos integrantes

Bienvenido(a) al proyecto. Esta guía es el punto de partida: qué es el sistema, qué leer, cómo dejar el proyecto corriendo y cuáles son las reglas del equipo. Léela completa antes de tu primer cambio.

## 1. Qué es el proyecto (en 1 minuto)

Sistema web para **priorizar la atención de pacientes en urgencias**. Registra pacientes y médicos, calcula una prioridad (`BAJA`, `MEDIA`, `ALTA`, `CRITICA`) según edad y gravedad, y muestra la cola ordenada.

- **Hoy**: aplicación **Spring Boot MVC** (Java 21 + Thymeleaf + MySQL) con CRUD de pacientes y médicos y validación de formularios.
- **Objetivo**: arquitectura hexagonal con eventos y cola en tiempo real ([02-arquitectura.md](02-arquitectura.md)).
- Proyecto de la materia Arquitectura de Software, Institución Universitaria Pascual Bravo.

## 2. Orden de lectura

| # | Documento | Para qué |
|---|---|---|
| 1 | [README](../README.md) | Visión general y enlaces |
| 2 | Esta guía | Reglas del equipo y primeros pasos |
| 3 | [04 · Flujo de trabajo Git](04-flujo-de-trabajo-git.md) | Ramas, PR, QA y despliegue — **obligatorio** |
| 4 | [05 · Convenciones de código](05-convenciones-de-codigo.md) | Nombres, capas, commits |
| 5 | [09 · Reglas de los campos](09-reglas-de-campos.md) | Qué acepta cada campo y cómo se valida — **obligatorio si tocas formularios** |
| 6 | [06 · Manual del sistema](06-manual-del-sistema.md) | Instalación y uso |
| 7 | [02 · Arquitectura](02-arquitectura.md) | Cómo está organizado el código |
| 8 | [08 · Mockups](08-mockups.md) | Diseño de cada pantalla en Figma |
| 9 | [01 · Visión y alcance](01-vision-y-alcance.md) · [07 · Roadmap](07-roadmap.md) | Requisitos y lo que falta |

## 3. Primer día: dejar el proyecto corriendo

- [ ] Pedir acceso al repositorio `wendy0509/urgencias-mvc` a la dueña (Wendy): *Settings → Collaborators*.
- [ ] Instalar **Git**, **JDK 21** y **XAMPP 8.2** (en Windows: `winget install --id Git.Git -e` y `winget install --id EclipseAdoptium.Temurin.21.JDK -e`). Cerrar y abrir la terminal después de instalar.
- [ ] Verificar: `git --version` y `java -version` (debe decir 21).
- [ ] Encender **MySQL** en XAMPP y crear la base: `CREATE DATABASE urgencias_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
- [ ] Clonar y arrancar:
  ```powershell
  git clone https://github.com/wendy0509/urgencias-mvc.git
  cd urgencias-mvc
  git checkout develop
  .\mvnw.cmd spring-boot:run
  ```
  Si tu MySQL está en el puerto **3306** (por defecto en XAMPP) y no en 3307, ver [06 · Manual, sección 3.1](06-manual-del-sistema.md#31-si-mysql-usa-el-puerto-3306).
- [ ] Abrir <http://localhost:8080>, registrar un médico y luego un paciente.
- [ ] Leer los documentos 3 a 5 de la tabla anterior.

## 4. Reglas del equipo

### Ramas y Pull Requests
1. **Nunca** se hace `push` directo a `main` ni a `develop`.
2. Todo trabajo va en una rama `feature/<tema>` que sale de `develop`.
3. Todo cambio entra por **Pull Request** hacia `develop`, aprobado por **al menos un integrante distinto al autor**.
4. **Los arreglos se agrupan**: se acumulan varios arreglos relacionados en la rama y se pregunta al equipo antes de abrir o actualizar el PR. No se abre un PR por cada detalle.
5. Ningún PR se une (*merge*) sin aprobación explícita.

### QA y despliegue
6. Cuando `develop` tiene lo necesario para una entrega, se crea `release/X.Y.Z` → esa rama es la que se prueba (**QA**). En `release/*` solo entran correcciones.
7. Cuando QA aprueba: PR `release/X.Y.Z` → `main`, etiqueta `vX.Y.Z` (despliegue / entrega) y se devuelve a `develop`.

### Autoría
8. Commits y PR quedan a nombre del **usuario de GitHub de quien hace el cambio**. En la descripción del PR se escribe al final `Autor(a): <usuario-github>`. No se agregan otras firmas ni textos automáticos; si una herramienta los agrega, se borran antes de pedir revisión.
9. Mensajes de commit con [Conventional Commits](05-convenciones-de-codigo.md#6-mensajes-de-commit): `feat:`, `fix:`, `docs:`…

### Datos y seguridad
10. Nunca subir contraseñas, tokens ni archivos `application-local.properties` / `.env`.
11. Nunca usar datos reales de pacientes. Datos de prueba siempre ficticios.

### Formularios y campos
12. Toda regla de un campo existe en **tres lugares**: entidad (`@Size`, `@Pattern`…), plantilla (`data-*`) y [09-reglas-de-campos.md](09-reglas-de-campos.md). Si cambias uno, cambias los tres.
13. Los campos de texto **siempre** tienen máximo de caracteres; los numéricos, solo dígitos y su rango.

### Documentación
14. Si tu cambio modifica rutas, base de datos, dependencias, reglas de campos o decisiones de arquitectura, actualiza el documento correspondiente **en el mismo PR** y agrega una línea en el [CHANGELOG](../CHANGELOG.md).

## 5. Tu primer cambio, paso a paso

```bash
git checkout develop
git pull origin develop
git checkout -b feature/mi-cambio

# ... trabajar, probar en http://localhost:8080 ...

git add .
git commit -m "fix(paciente): descripción corta en imperativo"
# acumular aquí los demás arreglos relacionados

# avisar al equipo y, con su visto bueno:
git push -u origin feature/mi-cambio
# abrir el PR hacia develop en GitHub con la plantilla
```

## 6. Dónde está cada cosa

| Necesito… | Archivo / carpeta |
|---|---|
| Cambiar una pantalla | `src/main/resources/templates/<entidad>/` |
| Cambiar una validación | `model/<Entidad>.java` + `templates/<entidad>/formulario.html` + `docs/09-reglas-de-campos.md` |
| Lógica en el navegador | `src/main/resources/static/js/` |
| Lógica de negocio (prioridad) | `service/PacienteService.java` |
| Rutas | `controller/` |
| Conexión a la base de datos | `src/main/resources/application.properties` |
| Mensajes de error de conversión | `src/main/resources/messages.properties` |
| Ver el diseño de una pantalla | [08-mockups.md](08-mockups.md) y Figma |

## 7. Contactos del equipo

| Integrante | Rol en el repositorio |
|---|---|
| Wendy Stefania Infante Vera (`wendy0509`) | Dueña del repositorio; configura permisos y protección de ramas |
| Karoll Dayanna Moreno Mahecha (`karollm567-ctrl`) | Colaboradora |
| Samuel López Marín | Colaborador |
