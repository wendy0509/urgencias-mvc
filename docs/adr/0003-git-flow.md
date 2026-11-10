# ADR-0003: Git Flow con Pull Request obligatorio

- Estado: Aceptada
- Fecha: 2026-11-09
- Autores: equipo del proyecto

## Contexto

Tres integrantes trabajan en paralelo sobre el mismo repositorio y se requieren entregas estables y trazables.

## Decisión

- Ramas permanentes `main` (entregas/despliegues) y `develop` (integración).
- Ramas temporales `feature/*` (y subramas `feature/<tema>/<subtema>`), `release/*` y `hotfix/*`.
- Todo cambio entra por Pull Request aprobado; sin push directo a `main` ni `develop`.
- Versionado semántico con etiquetas `vX.Y.Z` en `main`.

Detalle operativo en [04-flujo-de-trabajo-git.md](../04-flujo-de-trabajo-git.md).

## Alternativas consideradas

- **GitHub Flow** (solo `main` + ramas cortas): más simple, pero no separa lo entregado de lo que está en integración.
- **Trunk-based**: requiere CI madura y feature flags.

## Consecuencias

- (+) `main` siempre contiene una versión entregable.
- (+) Revisión cruzada de todo el código.
- (−) Más ramas y merges que gestionar; requiere mantener `develop` actualizada con `main` tras cada release o hotfix.
