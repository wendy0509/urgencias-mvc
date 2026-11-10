# Cómo contribuir

Si es tu primera vez en el proyecto, lee antes la [guía para nuevos integrantes](docs/00-guia-nuevo-integrante.md).

1. **Toma o crea un issue** describiendo la tarea (plantillas en *Issues → New issue*).
2. **Actualiza `develop`:** `git checkout develop && git pull origin develop`.
3. **Crea tu rama:** `git checkout -b feature/<numero-issue>-<tema>`.
4. **Trabaja con commits pequeños** siguiendo [Conventional Commits](docs/05-convenciones-de-codigo.md#6-mensajes-de-commit). Agrupa en la misma rama los arreglos relacionados.
5. **Verifica:** `./mvnw clean verify` y prueba manualmente las pantallas afectadas.
6. **Actualiza la documentación** si cambiaste rutas, BD, dependencias, reglas de campos ([docs/09](docs/09-reglas-de-campos.md)) o decisiones (ADR), y agrega una línea en el [CHANGELOG](CHANGELOG.md).
7. **Consulta al equipo** antes de subir: ¿se lanza el PR con este grupo de arreglos?
8. **Sube la rama y abre un Pull Request hacia `develop`** completando la plantilla. Al final de la descripción: `Autor(a): <tu-usuario-github>`.
9. **Espera la aprobación** de al menos un integrante distinto al autor. Nada se integra sin aprobación.
10. **Merge** con *Squash and merge* y borra la rama.

Entregas: `develop` → `release/X.Y.Z` (**QA**) → `main` (despliegue). Ver [flujo de trabajo Git](docs/04-flujo-de-trabajo-git.md).

## Reglas de oro

- Nunca hacer `push` directo a `main` ni a `develop`.
- Nunca subir contraseñas, tokens ni datos reales de pacientes.
- Si el PR rompe la compilación, no se aprueba.
- Un PR = un tema (un grupo coherente de arreglos). PRs pequeños se revisan mejor.
- Commits y PR solo con el usuario de GitHub de quien hace el cambio; sin firmas ni textos automáticos de herramientas.
- Toda regla de un campo vive en la entidad, en la plantilla y en `docs/09-reglas-de-campos.md`.
