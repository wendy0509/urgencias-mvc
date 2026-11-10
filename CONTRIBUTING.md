# Cómo contribuir

1. **Toma o crea un issue** describiendo la tarea (plantillas en *Issues → New issue*).
2. **Actualiza `develop`:** `git checkout develop && git pull origin develop`.
3. **Crea tu rama:** `git checkout -b feature/<numero-issue>-<tema>`.
4. **Trabaja con commits pequeños** siguiendo [Conventional Commits](docs/05-convenciones-de-codigo.md#6-mensajes-de-commit).
5. **Verifica:** `./mvnw clean verify` y prueba manualmente las pantallas afectadas.
6. **Actualiza la documentación** si cambiaste rutas, BD, dependencias o decisiones (ADR).
7. **Sube la rama y abre un Pull Request hacia `develop`** completando la plantilla.
8. **Espera la aprobación** de al menos un integrante distinto al autor. Nada se integra sin aprobación.
9. **Merge** con *Squash and merge* y borra la rama.

Entregas y despliegues a `main`: solo mediante `release/*` o `hotfix/*` (ver [flujo de trabajo Git](docs/04-flujo-de-trabajo-git.md)).

## Reglas de oro

- Nunca hacer `push` directo a `main` ni a `develop`.
- Nunca subir contraseñas, tokens ni datos reales de pacientes.
- Si el PR rompe la compilación, no se aprueba.
- Un PR = un tema. PRs pequeños se revisan mejor.
