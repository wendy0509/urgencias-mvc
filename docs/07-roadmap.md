# 07 · Roadmap

## Fases

| Fase | Versión | Rama de entrega | Objetivo | Estado |
|---|---|---|---|---|
| 0 | — | `feature/documentacion-base` | Documentación, flujo Git y convenciones | 🟡 En curso |
| 1 | `v0.1.0` | `release/0.1.0` | Entrega MVC: CRUD Paciente/Médico con MySQL, validaciones, manual | 🟡 En curso |
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

## Deuda técnica identificada (revisión del 9 nov 2026)

Cada punto debería convertirse en un issue y corregirse en su propia rama:

| # | Hallazgo | Impacto | Rama sugerida |
|---|---|---|---|
| 1 | La plantilla se llama `pacientes/fomulario.html` (falta la "r") pero el controlador devuelve `pacientes/formulario` | `/pacientes/nuevo` y `/pacientes/{id}/editar` fallan con `TemplateInputException` | `feature/fix-plantilla-formulario-paciente` |
| 2 | `calcularPrioridad` usa edad `>= 70` y `<= 5`; el RF02 define `>= 65` y `< 5` | Prioridad distinta a la documentada | `feature/motor-prioridad` |
| 3 | `prioridad` es `String` libre; debería ser un `enum` (`BAJA`, `MEDIA`, `ALTA`, `CRITICA`) | Riesgo de valores inconsistentes | `feature/motor-prioridad` |
| 4 | La cola se ordena por `nivelGravedad`, no por la `prioridad` calculada | El orden puede no reflejar la regla de edad | `feature/motor-prioridad` |
| 5 | El comentario de `alergias` cita "RF20", que no existe en el documento de requisitos | Trazabilidad | `feature/motor-prioridad` o actualizar requisitos |
| 6 | Sólo existe la prueba `contextLoads` | Sin red de seguridad para refactorizar | `feature/pruebas-servicios` |
| 7 | `pom.xml` con `<name/>`, `<description/>`, `<developers/>` vacíos | Metadatos incompletos | `feature/documentacion-base` (opcional) |
