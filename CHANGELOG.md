# Changelog

Todos los cambios relevantes del proyecto se documentan aquí.
Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y [Versionado Semántico](https://semver.org/lang/es/).

## [Sin publicar]

### Añadido
- Documentación del proyecto: visión y alcance, arquitectura, stack, flujo Git (con etapa de QA), convenciones, manual del sistema, roadmap y ADR.
- Guía para nuevos integrantes (`docs/00-guia-nuevo-integrante.md`) con reglas del equipo.
- Diccionario de reglas de los campos (`docs/09-reglas-de-campos.md`).
- Mockups en Figma de las 8 pantallas (`docs/08-mockups.md`, `docs/mockups/`).
- Plantillas de Pull Request e issues.
- Informes de QA en `docs/qa/` (primero: validaciones, PR #4).
- Validación de formularios en tiempo real (`static/js/validacion-formularios.js`):
  - Documento: solo números, 5 a 20 dígitos.
  - Edad: solo números, máximo 3 dígitos, hasta 120.
  - Nivel de gravedad: solo un número del 1 al 5.
  - Nombres de paciente y médico: solo letras, máximo 100 caracteres, con contador.
  - Especialidad: solo letras, máximo 80; correo: máximo 100 y formato válido.
  - Motivo de consulta y alergias: máximo 255 con contador.
- Reglas equivalentes en las entidades (`@Size`, `@Pattern`) y `messages.properties` con mensajes en español.
- Proyecto Spring Boot MVC inicial con entidades `Paciente` y `Medico` (relación muchos a uno), CRUD, validaciones y estilos básicos.
- Cálculo simplificado de prioridad (RF02).

### Corregido
- Error 500 (`TemplateInputException`) al registrar o editar un paciente: la plantilla se llamaba `fomulario.html`.
- Correo: al pegar más de 100 caracteres no se mostraba el mensaje de límite (detectado en QA).
