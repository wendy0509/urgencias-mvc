# 04 · Flujo de trabajo Git

El equipo usa **Git Flow**. Todo el trabajo se integra primero en `develop`; `main` sólo recibe versiones listas para entregar o desplegar.

## 1. Ramas

| Rama | Sale de | Se integra en | Propósito | Vida |
|---|---|---|---|---|
| `main` | — | — | Código entregado / desplegado. Cada merge = una versión etiquetada (`v0.1.0`) | Permanente |
| `develop` | `main` (una sola vez) | — | Integración del trabajo diario. Siempre debe compilar | Permanente |
| `feature/<tema>` | `develop` | `develop` | Una funcionalidad, documento o refactor | Temporal |
| `feature/<tema>/<subtema>` | `feature/<tema>` | `feature/<tema>` | Subrama para dividir una feature grande entre integrantes | Temporal |
| `release/<versión>` | `develop` | `main` **y** `develop` | Preparar una entrega: ajustes finales, versión, CHANGELOG | Temporal |
| `hotfix/<versión>` | `main` | `main` **y** `develop` | Corrección urgente sobre lo ya entregado | Temporal |

```
main      ●────────────────────────────●─────────────●  (v0.1.0)   (v0.1.1)
           \                          / \           /
release     \              ●────●────●   \         /
             \            /               \       /
hotfix        \          /                 ●─────●
               \        /                         \
develop         ●──●───●──────●───────●────────────●──
                    \    /     \     /
feature              ●──●       ●───●
```

## 2. Nombres de ramas

- Minúsculas, palabras separadas por guiones, sin tildes ni `ñ`.
- Prefijo obligatorio: `feature/`, `release/`, `hotfix/`.
- Si existe issue, incluir el número: `feature/12-cola-tiempo-real`.

| Ejemplo | Uso |
|---|---|
| `feature/documentacion-base` | Documentación inicial del repositorio |
| `feature/rf01-registro-signos-vitales` | Requisito funcional concreto |
| `feature/motor-prioridad` | Feature grande… |
| `feature/motor-prioridad/regla-edad` | …dividida en subramas |
| `release/0.1.0` | Entrega MVC |
| `hotfix/0.1.1` | Corrección sobre `v0.1.0` |

## 3. Ciclo de trabajo de una feature

```bash
# 1. Actualizar develop
git checkout develop
git pull origin develop

# 2. Crear la rama
git checkout -b feature/rf05-editar-informacion-clinica

# 3. Trabajar con commits pequeños (ver convenciones)
git add .
git commit -m "feat(paciente): permitir editar signos vitales"

# 4. Antes de abrir el PR, traer lo último de develop
git fetch origin
git rebase origin/develop      # o: git merge origin/develop

# 5. Verificar que compila y pasan las pruebas
./mvnw clean verify

# 6. Subir y abrir Pull Request hacia develop
git push -u origin feature/rf05-editar-informacion-clinica
```

### Subramas

Cuando una feature es grande y la trabajan varios integrantes:

1. Se crea `feature/<tema>` desde `develop`.
2. Cada integrante crea `feature/<tema>/<subtema>` desde `feature/<tema>`.
3. Las subramas hacen PR hacia `feature/<tema>`.
4. Cuando la feature completa funciona, `feature/<tema>` hace PR hacia `develop`.

> Git no permite tener a la vez una rama `feature/x` y otra `feature/x/y` en algunos clientes antiguos; si ocurre, usar `feature/x--y`.

### Agrupar arreglos antes del PR

Los arreglos pequeños que se van encontrando al probar **no** se suben uno por uno:

1. Se hacen en la misma rama `feature/*`, un commit por arreglo.
2. Se prueban en local.
3. Cuando hay un grupo de arreglos listo, se **consulta al equipo** si se lanza el PR (o si se actualiza uno ya abierto).
4. Con el visto bueno, `git push` y PR hacia `develop`.

## 4. Release (QA y despliegue)

El ciclo de entrega tiene tres etapas:

```
develop ──▶ release/X.Y.Z ──(QA aprueba)──▶ main  (vX.Y.Z = despliegue)
                 │
                 └── correcciones de QA, solo en esta rama
```

| Etapa | Rama | Quién | Qué se hace |
|---|---|---|---|
| Integración | `develop` | Equipo | Se unen las features aprobadas |
| **QA** | `release/X.Y.Z` | Integrante distinto al que desarrolló | Se prueba la aplicación completa con el [manual](06-manual-del-sistema.md) y las [reglas de campos](09-reglas-de-campos.md). Los errores se corrigen **en la rama release** |
| Despliegue / entrega | `main` | Dueña del repositorio | Merge de la release, etiqueta `vX.Y.Z` |

### Lista de verificación de QA

- [ ] La aplicación arranca sin errores y crea las tablas.
- [ ] CRUD completo de médicos: registrar, listar, ver, editar, eliminar (y bloqueo al eliminar con pacientes).
- [ ] CRUD completo de pacientes: registrar, listar, ver, editar, eliminar.
- [ ] Cada campo cumple su regla de [09-reglas-de-campos.md](09-reglas-de-campos.md) (bloqueos, límites, mensajes).
- [ ] Validación del servidor: con JavaScript desactivado en el navegador, los datos inválidos se rechazan con mensaje.
- [ ] Documento duplicado rechazado.
- [ ] Lista de pacientes ordenada por gravedad y hora de llegada.
- [ ] Las pantallas coinciden con los [mockups](08-mockups.md).

Cada ronda de QA se documenta en `docs/qa/` (casos, resultados, defectos y conclusión). Ejemplo: [QA-PR4-validaciones.md](qa/QA-PR4-validaciones.md).

### Pasos

```bash
git checkout develop && git pull
git checkout -b release/0.1.0
# - Actualizar <version> en pom.xml (quitar -SNAPSHOT)
# - Actualizar CHANGELOG.md
# - Solo correcciones menores: nada de features nuevas
git push -u origin release/0.1.0
```

1. QA prueba la rama `release/0.1.0` con la lista de verificación. Las correcciones se hacen en esa misma rama.
2. Cuando QA aprueba: PR `release/0.1.0` → `main`. Al aprobarse, crear la etiqueta: `git tag -a v0.1.0 -m "Entrega MVC"` y `git push origin v0.1.0`.
3. PR `release/0.1.0` → `develop` (o `main` → `develop`) para no perder los ajustes de QA.
4. En `develop`, subir la versión a la siguiente `-SNAPSHOT` (p. ej. `0.2.0-SNAPSHOT`).

## 5. Hotfix

```bash
git checkout main && git pull
git checkout -b hotfix/0.1.1
# corregir, actualizar versión y CHANGELOG
```
PR a `main` (etiqueta `v0.1.1`) y luego a `develop`.

## 6. Pull Requests

**Ningún cambio llega a `develop` ni a `main` sin Pull Request aprobado.** No se hace `push` directo a esas ramas.

| Regla | `develop` | `main` |
|---|---|---|
| Push directo | ❌ | ❌ |
| PR obligatorio | ✅ | ✅ (solo desde `release/*` o `hotfix/*`) |
| Aprobaciones mínimas | 1 integrante distinto al autor | 2 integrantes |
| Debe compilar (`./mvnw clean verify`) | ✅ | ✅ |
| Tipo de merge | *Squash and merge* (features) | *Merge commit* (conserva historia de la release) |
| Borrar rama tras el merge | ✅ | ✅ |

Checklist del PR: ver [.github/pull_request_template.md](../.github/pull_request_template.md).

### Autorización de cambios

- Todo PR y toda actualización de un PR existente se presenta al equipo y **se aprueba explícitamente antes de hacer merge**.
- En la descripción del PR se indica únicamente el usuario de GitHub de quien lo crea (`Autor(a): <usuario>`). Commits y PR no llevan otras firmas ni textos automáticos; si una herramienta los agrega, se borran antes de pedir revisión.
- Antes de abrir o actualizar un PR se consulta al equipo (ver *Agrupar arreglos antes del PR*).

## 7. Versionado

[Versionado semántico](https://semver.org/lang/es/) `MAYOR.MENOR.PARCHE`:

- **MAYOR**: cambio incompatible (p. ej. migración completa a hexagonal → `1.0.0`).
- **MENOR**: nueva funcionalidad compatible.
- **PARCHE**: corrección de errores.

Mientras el proyecto esté en `0.x`, la API puede cambiar entre versiones menores.

## 8. Protección de ramas (configurar en GitHub)

*Settings → Branches → Add branch ruleset* para `main` y `develop`:

- Require a pull request before merging (aprobaciones según la tabla del punto 6).
- Dismiss stale approvals when new commits are pushed.
- Block force pushes y restrict deletions.
- (Cuando exista CI) Require status checks to pass.

Además: *Settings → General → Default branch* = `develop`, para que los PR apunten ahí por defecto.
