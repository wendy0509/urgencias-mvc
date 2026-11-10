# Informe de QA — PR #4: Validación de formularios en tiempo real

| Dato | Valor |
|---|---|
| PR | [#4](https://github.com/wendy0509/urgencias-mvc/pull/4) `feature/validacion-tiempo-real` → `develop` |
| Fecha | 2026-11-10 |
| Responsable | karollm567-ctrl |
| Alcance | Formularios de paciente y médico: bloqueos, límites, mensajes y envío |
| Referencia | [09 · Reglas de los campos](../09-reglas-de-campos.md) |
| Resultado | **29 / 29 casos pasan** — sin errores de JavaScript en consola |

## 1. Entorno de prueba

- **Automatizado:** Chromium (Playwright) sobre las plantillas `pacientes/formulario.html` y `medicos/formulario.html` con el script `static/js/validacion-formularios.js` de la rama.
- **Manual:** aplicación local (`spring-boot:run`, MySQL de XAMPP) — ver sección 4.

## 2. Resultados

| ID | Formulario | Campo | Caso | Resultado obtenido (valor · mensaje) | Estado |
|---|---|---|---|---|---|
| QA-01 | Paciente | nombre | Nombre y apellidos válidos | "María José…(29)" | ✅ Pasa |
| QA-02 | Paciente | nombre | Números y símbolos no entran | "Ana Luz" | ✅ Pasa |
| QA-03 | Paciente | nombre | Máximo 100 caracteres | "cccccccccc…(100)" · El nombre admite máximo 100 caracteres | ✅ Pasa |
| QA-04 | Paciente | nombre | Pegar 150 caracteres | "aaaaaaaaaa…(100)" · El nombre admite máximo 100 caracteres | ✅ Pasa |
| QA-05 | Paciente | nombre | Vacío es obligatorio | "" · El nombre es obligatorio | ✅ Pasa |
| QA-06 | Paciente | documento | Letras no entran | "11" · El documento solo debe contener números | ✅ Pasa |
| QA-07 | Paciente | documento | Pegar con puntos y guiones | "102034567" · El documento solo debe contener números | ✅ Pasa |
| QA-08 | Paciente | documento | Menos de 5 dígitos | "123" · El documento debe tener entre 5 y 20 dígitos | ✅ Pasa |
| QA-09 | Paciente | documento | Máximo 20 dígitos | "11111111111111111111" · El documento admite máximo 20 dígitos | ✅ Pasa |
| QA-10 | Paciente | documento | 10 dígitos válido | "1020345678" | ✅ Pasa |
| QA-11 | Paciente | edad | Máximo 3 dígitos | "114" · La edad admite máximo 3 dígitos | ✅ Pasa |
| QA-12 | Paciente | edad | Letras no entran | "45" | ✅ Pasa |
| QA-13 | Paciente | edad | Mayor de 120 | "150" · La edad máxima es 120 | ✅ Pasa |
| QA-14 | Paciente | edad | 105 años (excepción) válido | "105" | ✅ Pasa |
| QA-15 | Paciente | edad | Signo negativo no entra | "5" | ✅ Pasa |
| QA-16 | Paciente | nivel de gravedad | 10 → solo 1 | "1" · Solo se permiten números del 1 al 5 | ✅ Pasa |
| QA-17 | Paciente | nivel de gravedad | 7 no entra | "" · Solo se permiten números del 1 al 5 | ✅ Pasa |
| QA-18 | Paciente | nivel de gravedad | 0 no entra | "" · Solo se permiten números del 1 al 5 | ✅ Pasa |
| QA-19 | Paciente | nivel de gravedad | 4 válido | "4" | ✅ Pasa |
| QA-20 | Paciente | motivo de consulta | Máximo 255 | "xxxxxxxxxx…(255)" · Máximo 255 caracteres | ✅ Pasa |
| QA-21 | Paciente | alergias | Opcional vacío sin error | "" | ✅ Pasa |
| QA-22 | Paciente | médico asignado | Sin médico al guardar | "" · Debe seleccionar un médico | ✅ Pasa |
| QA-23 | Paciente | formulario | Guardar vacío no envía y enfoca el primer error | envió: false · foco: nombre | ✅ Pasa |
| QA-24 | Médico | nombre | Máximo 100 y solo letras | "Dr. Andrés Ruiz" | ✅ Pasa |
| QA-25 | Médico | especialidad | Números no entran | "Medicina nterna" | ✅ Pasa |
| QA-26 | Médico | especialidad | Máximo 80 | "mmmmmmmmmm…(80)" · Máximo 80 caracteres | ✅ Pasa |
| QA-27 | Médico | correo | Formato inválido | "a@b" · Formato de correo inválido | ✅ Pasa |
| QA-28 | Médico | correo | Correo válido | "pcastano@urgencias.co" | ✅ Pasa |
| QA-29 | Médico | correo | Pegar 105 caracteres → 100 | "aaaaaaaaaa…(100)" · Máximo 100 caracteres | ✅ Pasa |

## 3. Defectos encontrados

| ID | Defecto | Severidad | Estado |
|---|---|---|---|
| D-01 | Error 500 `TemplateInputException` al abrir `/pacientes/nuevo` (plantilla `fomulario.html`) | Alta | ✅ Corregido en PR #3 (ya en `develop`) |
| D-02 | Documento aceptaba letras (`11saaaa`) | Media | ✅ Corregido (QA-06, QA-07) |
| D-03 | Edad aceptaba más de 3 dígitos (`11452542`) | Media | ✅ Corregido (QA-11) |
| D-04 | Gravedad permitía escribir valores fuera de 1–5 (`10`) | Media | ✅ Corregido (QA-16 a QA-18) |
| D-05 | Nombre sin límite de caracteres | Media | ✅ Corregido (QA-03, QA-04) |
| D-06 | Correo: al pegar más de 100 caracteres el script lanzaba una excepción (`setSelectionRange` no se admite en `type="email"`) y no mostraba el mensaje | Baja | ✅ Corregido en `65a5b70` (QA-29) |

## 4. Verificación manual en la aplicación (antes de aprobar el merge)

Estos puntos requieren la aplicación corriendo con base de datos y los debe confirmar quien revisa el PR:

- [ ] La aplicación compila y arranca (`.\mvnw.cmd spring-boot:run`) — incluye las nuevas anotaciones `@Pattern` y `messages.properties`.
- [ ] Registrar un paciente válido → aparece en `/pacientes` con su prioridad.
- [ ] Editar ese paciente → el formulario carga con sus datos y guarda.
- [ ] Registrar un segundo paciente con el **mismo documento** → "Ya existe un paciente con ese documento".
- [ ] Con **JavaScript desactivado** (o enviando datos inválidos), el servidor rechaza: nombre con números, documento con letras, gravedad 7 — con mensajes en español.
- [ ] Registrar y editar un médico; especialidad y correo con las reglas nuevas.
- [ ] Recargar con **Ctrl + F5** si el navegador conserva una versión anterior del script.

## 5. Conclusión

La validación en el navegador cumple las reglas de [09-reglas-de-campos.md](../09-reglas-de-campos.md) en 29 de 29 casos. Se recomienda **aprobar el merge a `develop`** una vez confirmada la verificación manual de la sección 4. Después del merge, estas reglas entran en la lista de verificación de QA de `release/0.1.0`.
