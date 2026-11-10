# 01 · Visión y alcance

## 1. Problema

Los servicios de urgencias reciben a diario pacientes con condiciones muy diversas que deben clasificarse con rapidez y precisión. Con alta demanda, la clasificación manual genera demoras, desorden en la fila de espera y pérdida de visibilidad sobre el estado real de cada caso.

**Problema central:** no existe una herramienta que apoye, de forma ágil y confiable, la priorización de pacientes con criterios clínicos y administrativos objetivos (edad, nivel de gravedad, signos vitales y tiempo de espera), de modo que la priorización no dependa solo de la percepción individual del personal en momentos de alta presión.

**A quién afecta:** directamente al personal médico, de enfermería y de admisión; indirectamente a los pacientes y a la administración del centro asistencial (riesgo legal por falta de trazabilidad).

## 2. Objetivo del software

Ofrecer una vista organizada, actualizada en tiempo real y trazable del estado de prioridad de cada paciente, que apoye —sin reemplazar— el criterio profesional, y que conserve un registro completo del proceso de atención.

## 3. Usuarios y roles

| Rol | Responsabilidades en el sistema |
|---|---|
| Admisión / Enfermería | Registra pacientes, captura signos vitales y gravedad inicial |
| Personal médico | Consulta la cola priorizada, actualiza datos clínicos, cierra la atención |
| Administrador / Jefe médico | Configura reglas de clasificación, gestiona usuarios, genera reportes y auditoría |

## 4. Alcance

**Dentro del alcance inicial:** registro concurrente de pacientes, cálculo y actualización dinámica de prioridad, cola de atención en tiempo real, edición de información clínica, cierre de atención, historial para trazabilidad, reportes operativos, configuración de reglas de clasificación, gestión de usuarios y roles.

**Fuera del alcance inicial:** integración con historias clínicas electrónicas externas, facturación e interoperabilidad con otras instituciones.

## 5. Requisitos funcionales

| ID | Requisito | Actor | Prioridad |
|---|---|---|---|
| RF01 | **Registrar paciente**: edad, motivo de consulta, gravedad, signos vitales y hora de llegada, desde múltiples terminales | Admisión / Enfermería | Alta |
| RF02 | **Clasificar prioridad inicial** según edad (<5 años, ≥65 años), gravedad y signos vitales | Sistema / Enfermería | Alta |
| RF03 | **Actualizar prioridad dinámicamente** al cambiar signos vitales o superar umbrales de tiempo de espera | Sistema | Alta |
| RF04 | **Visualizar cola de atención** en tiempo real, ordenada por prioridad, sin refrescar manualmente | Médico / Enfermería | Alta |
| RF05 | **Editar información clínica** (signos vitales, gravedad, observaciones), recalculando prioridad | Médico / Enfermería | Media |
| RF06 | **Registrar atención finalizada** con hora de cierre, retirando al paciente de la cola activa | Médico | Media |
| RF07 | **Consultar historial** de atenciones y cambios de prioridad | Administrador / Auditoría | Media |
| RF08 | **Generar reportes operativos** (tiempos de espera, volumen, distribución de prioridades) y exportarlos | Administrador | Media |
| RF09 | **Configurar reglas de clasificación** (criterios, pesos, umbrales) sin modificar código | Administrador / Jefe médico | Alta |
| RF10 | **Gestionar usuarios y roles** con autenticación | Administrador | Alta |

## 6. Requisitos no funcionales

| ID | Requisito | Atributo | Prioridad | Meta |
|---|---|---|---|---|
| RNF01 | Disponibilidad | Disponibilidad | Alta | 24/7, ≥ 99.9 %, redundancia ante fallo de un nodo |
| RNF02 | Tiempo de respuesta | Rendimiento | Alta | Operaciones < 2 s; recálculo y reordenamiento de cola < 500 ms |
| RNF03 | Concurrencia | Concurrencia | Alta | Registro y consulta simultánea sin inconsistencias ni pérdida de registros |
| RNF04 | Seguridad y confidencialidad | Seguridad | Alta | TLS 1.3 en tránsito, AES-256 en reposo, RBAC. Ley 1581 de 2012, Res. 1995 de 1999 |
| RNF05 | Escalabilidad | Escalabilidad | Media | Escalamiento horizontal sin degradar tiempos |
| RNF06 | Mantenibilidad | Mantenibilidad | Media | Cambiar reglas de clasificación sin afectar otros módulos ni redesplegar todo |
| RNF07 | Usabilidad | Usabilidad | Media | Registrar un paciente en menos de 5 pasos |
| RNF08 | Trazabilidad | Auditabilidad | Media | Historial inmutable de cambios con usuario responsable |

## 7. Atributos de calidad priorizados

1. **Disponibilidad** — una caída obliga a volver al proceso manual en plena situación crítica.
2. **Seguridad y confidencialidad** — una fuga de información clínica es irreversible.
3. **Rendimiento y concurrencia** — la prioridad sólo sirve si está actualizada cuando se decide.
4. **Mantenibilidad** — los protocolos de triage cambian entre instituciones y en el tiempo.
5. **Escalabilidad** — se aborda de forma incremental, sin que la arquitectura lo impida.

## 8. Paradigmas de programación

- **Orientado a objetos** (base): entidades del dominio con estado y comportamiento; organización en capas.
- **Declarativo / basado en reglas**: el motor de clasificación expresa reglas independientes (RF09, RNF06).
- **Reactivo / orientado a eventos**: actualización dinámica y cola en tiempo real (RF03, RF04, RNF03).
- **Funcional (puntual)**: funciones puras para calcular el puntaje de prioridad, fáciles de probar.

## 9. Principios SOLID aplicados

| Principio | Aplicación |
|---|---|
| **S** — Responsabilidad única | `ClasificadorPrioridad`, `RegistroPaciente` y `NotificadorCola` como clases separadas |
| **O** — Abierto/cerrado | Reglas como estrategias (patrón Strategy): un nuevo protocolo = una nueva clase de regla |
| **L** — Sustitución de Liskov | Toda `ReglaDePrioridad` (`ReglaEdad`, `ReglaGravedad`, `ReglaSignosVitales`, `ReglaTiempoEspera`) es intercambiable |
| **I** — Segregación de interfaces | `IPacienteReader`, `ITriageWriter`, `IReportService` según rol y operación |
| **D** — Inversión de dependencias | El motor depende de `RepositorioPacientes` (abstracción), no de una BD concreta |

> Fuente: documento *Requerimientos App Clasificación de urgencias* y presentación del equipo.
