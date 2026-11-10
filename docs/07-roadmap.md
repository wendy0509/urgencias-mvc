# 07 · Roadmap

## Fases

| Fase | Versión | Rama de entrega | Objetivo | Estado |
|---|---|---|---|---|
| 0 | — | `feature/documentacion-base` | Documentación, flujo Git, convenciones, guía de integrantes y reglas de campos | ✅ Integrado (PR #1) |
| 1 | `v0.1.0` | `release/0.1.0` | Entrega MVC: CRUD Paciente/Médico con MySQL, validaciones en tiempo real, manual → **QA** → `main` | 🟡 En curso |
| 2 | `v0.2.0` | `release/0.2.0` | Completar requisitos clínicos en MVC: signos vitales, cierre de atención, historial | ⚪ Pendiente |
| 3 | `v0.3.0` | `release/0.3.0` | Motor de prioridad con reglas (Strategy) y pruebas unitarias | ⚪ Pendiente |
| 4 | `v0.4.0` | `release/0.4.0` | Seguridad: usuarios, roles, login (RF10, RNF04) | ⚪ Pendiente |
| 5 | `v1.0.0` | `release/1.0.0` | Migración a hexagonal + eventos + cola en tiempo real | ⚪ Pendiente |

## Mapeo requisito → rama sugerida

| Requisito | Rama `feature/*` | Fase | Estado en el código |
|---|---|---|---|
| RF01 Registrar paciente | `feature/rf01-registro-signos-vitales` | 2 | Parcial: falta capturar signos vitales |
| RF02 Clasificar prioridad inicial | `feature/motor-prioridad` (subramas `/regla-edad`, `/regla-gravedad`, `/regla-signos-vitales`) | 3 | Parcial: versión simplificada en `PacienteService` |
| RF03 Actualizar prioridad dinámicamente | `feature/rf03-recalculo-por-tiempo-espera` | 3 | No iniciado |
| RF04 Cola en tiempo real | `feature/rf04-cola-tiempo-real` | 5 | Parcial: lista ordenada sin actualización automática |
| RF05 Editar información clínica | `feature/rf05-editar-informacion-clinica` | 2 | Parcial: edición general de paciente |
| RF06 Registrar atención finalizada | `feature/rf06-cierre-atencion` | 2 | Parcial: existe estado `ATENDIDO`, falta hora de cierre |
| RF07 Historial de atención | `feature/rf07-historial` | 2 | No iniciado |
| RF08 Reportes operativos | `feature/rf08-reportes` | 4 | No iniciado |
| RF09 Configurar reglas | `feature/rf09-configuracion-reglas` | 3 | No iniciado |
| RF10 Usuarios y roles | `feature/rf10-usuarios-roles` | 4 | No iniciado |

## Trabajo en curso de la fase 1

| Rama | PR | Contenido | Estado |
|---|---|---|---|
| `feature/documentacion-base` | #1 | Documentación base, guía de nuevos integrantes, reglas de campos | ✅ Integrado en `develop` |
| `feature/mockups-figma` | #2 | Mockups de las 8 pantallas | En revisión |
| `feature/fix-plantilla-formulario-paciente` | #3 | Corrige error 500 al registrar/editar paciente | ✅ Integrado en `develop` |
| `feature/validacion-tiempo-real` | #4 | Validación en tiempo real, bloqueos y límites de todos los campos | QA aprobado (29/29) — pendiente merge. [Informe](qa/QA-PR4-validaciones.md) |

Después de aprobar estos PR: crear `release/0.1.0` para QA.

## Deuda técnica identificada (revisión del 9 nov 2026)

Cada punto debería convertirse en un issue y corregirse en su propia rama:

| # | Hallazgo | Impacto | Rama sugerida |
|---|---|---|---|
| 1 | ~~La plantilla se llama `pacientes/fomulario.html` pero el controlador devuelve `pacientes/formulario`~~ | ✅ Corregido en PR #3 (confirmado en pruebas: error 500 al registrar paciente) | `feature/fix-plantilla-formulario-paciente` |
| 2 | `calcularPrioridad` usa edad `>= 70` y `<= 5`; el RF02 define `>= 65` y `< 5` | Prioridad distinta a la documentada | `feature/motor-prioridad` |
| 3 | `prioridad` es `String` libre; debería ser un `enum` (`BAJA`, `MEDIA`, `ALTA`, `CRITICA`) | Riesgo de valores inconsistentes | `feature/motor-prioridad` |
| 4 | La cola se ordena por `nivelGravedad`, no por la `prioridad` calculada | El orden puede no reflejar la regla de edad | `feature/motor-prioridad` |
| 5 | ~~El comentario de `alergias` cita "RF20", que no existe en el documento de requisitos~~ | ✅ Comentario eliminado en PR #4 | `feature/validacion-tiempo-real` |
| 6 | Sólo existe la prueba `contextLoads` | Sin red de seguridad para refactorizar | `feature/pruebas-servicios` |
| 7 | `pom.xml` con `<name/>`, `<description/>`, `<developers/>` vacíos | Metadatos incompletos | `feature/documentacion-base` (opcional) |
| 8 | Bases de datos creadas antes del límite de 100 caracteres en `correo` conservan la columna en 255 (`ddl-auto=update` no reduce columnas) | Ninguno funcional: la validación limita a 100 | Migraciones con Flyway (fase 5) |
| 9 | No hay pruebas automáticas del JavaScript de validación | Las reglas se prueban manualmente en QA | `feature/pruebas-servicios` |
