# 09 · Reglas de los campos (diccionario de datos)

Este documento es la **fuente única** de las reglas de cada campo de los formularios. Si una regla cambia, se cambia en los lugares indicados en la sección 4 y aquí.

## 1. Cómo se valida

Cada regla se aplica en **dos capas**, con los mismos mensajes:

| Capa | Dónde | Qué hace |
|---|---|---|
| **Navegador** (tiempo real) | `src/main/resources/static/js/validacion-formularios.js` + atributos `data-*` en las plantillas | Muestra el error en rojo mientras se escribe. **Bloquea** al escribir o pegar los caracteres no permitidos (letras en números, más caracteres del máximo, valores fuera de rango). No deja enviar el formulario si hay errores. |
| **Servidor** (respaldo) | Anotaciones de Bean Validation en `model/Paciente.java` y `model/Medico.java`, más reglas en el controlador | Rechaza datos inválidos aunque alguien se salte el navegador. Los errores se muestran bajo cada campo con `th:errors`. |

Tipos de regla:
- **Bloquea**: el carácter o valor no entra en el campo; aparece el mensaje.
- **Valida**: el valor se puede escribir, pero se marca en rojo y no deja guardar.

## 2. Paciente — `/pacientes/nuevo`, `/pacientes/{id}/editar`

| Campo | Columna BD | Tipo | Obligatorio | Reglas | Mensajes |
|---|---|---|---|---|---|
| Nombre completo (nombres y apellidos) | `nombre` VARCHAR(100) | Texto | Sí | **Bloquea**: máximo **100** caracteres (con contador). **Bloquea**: solo letras (incluye tildes y ñ), espacios, punto, guion y apóstrofo | El nombre es obligatorio · El nombre admite máximo 100 caracteres · El nombre solo debe contener letras |
| Documento | `documento` VARCHAR(20), **único** | Texto numérico | Sí | **Bloquea**: solo dígitos 0–9. **Bloquea**: máximo **20** dígitos. **Valida**: mínimo **5** dígitos. **Valida** (servidor): no puede repetirse | El documento es obligatorio · El documento solo debe contener números · El documento admite máximo 20 dígitos · El documento debe tener entre 5 y 20 dígitos · Ya existe un paciente con ese documento |
| Edad | `edad` INT | Entero | Sí | **Bloquea**: solo dígitos. **Bloquea**: máximo **3** dígitos. **Valida**: entre **0 y 120** (se permiten mayores de 100 como excepción) | La edad es obligatoria · La edad solo debe contener números · La edad admite máximo 3 dígitos · La edad máxima es 120 |
| Motivo de consulta | `motivo_consulta` VARCHAR(255) | Texto | Sí | **Bloquea**: máximo **255** caracteres (con contador) | El motivo de consulta es obligatorio · Máximo 255 caracteres |
| Nivel de gravedad | `nivel_gravedad` INT | Entero | Sí | **Bloquea**: solo **un dígito del 1 al 5** (no deja escribir 0, 6–9 ni dos dígitos) | La gravedad es obligatoria · Solo se permiten números del 1 al 5 |
| Alergias / antecedentes críticos | `alergias` VARCHAR(255) | Texto | No | **Bloquea**: máximo **255** caracteres (con contador) | Máximo 255 caracteres |
| Estado | `estado` VARCHAR | Lista | Sí (por defecto `EN_ESPERA`) | Valores: `EN_ESPERA`, `EN_ATENCION`, `ATENDIDO` | — |
| Médico asignado | `medico_id` BIGINT (FK) | Lista | Sí | Debe elegirse un médico existente | Debe seleccionar un médico |
| Hora de llegada | `fecha_llegada` DATETIME | Automático | — | La asigna el sistema al registrar; se conserva al editar | — |
| Prioridad | `prioridad` VARCHAR | Automático | — | La calcula el sistema (`BAJA`, `MEDIA`, `ALTA`, `CRITICA`) a partir de edad y gravedad | — |

## 3. Médico — `/medicos/nuevo`, `/medicos/{id}/editar`

| Campo | Columna BD | Tipo | Obligatorio | Reglas | Mensajes |
|---|---|---|---|---|---|
| Nombre completo (nombres y apellidos) | `nombre` VARCHAR(100) | Texto | Sí | **Bloquea**: máximo **100** caracteres (con contador). **Bloquea**: solo letras, espacios, punto, guion y apóstrofo | El nombre es obligatorio · El nombre admite máximo 100 caracteres · El nombre solo debe contener letras |
| Especialidad | `especialidad` VARCHAR(80) | Texto | Sí | **Bloquea**: máximo **80** caracteres (con contador). **Bloquea**: solo letras | La especialidad es obligatoria · Máximo 80 caracteres · La especialidad solo debe contener letras |
| Correo | `correo` VARCHAR(100) | Correo | Sí | **Bloquea**: máximo **100** caracteres. **Valida**: formato `usuario@dominio.ext` | El correo es obligatorio · Formato de correo inválido |
| Disponible | `disponible` BIT | Casilla | — | Marcado por defecto | — |

Regla de negocio: **no se puede eliminar un médico con pacientes asignados** → "No se puede eliminar el médico porque tiene pacientes asignados".

## 4. Cómo agregar o cambiar una regla

Una regla siempre se cambia en **los tres lugares** y en este documento, en la misma rama `feature/*`:

1. **Entidad** (`model/*.java`): anotación de Bean Validation con el mensaje en español.
   ```java
   @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
   @Pattern(regexp = "[\\p{L} .'-]*", message = "El nombre solo debe contener letras")
   private String nombre;
   ```
2. **Plantilla** (`templates/*/formulario.html`): atributos `data-*` en el campo, con el **mismo** mensaje.
   ```html
   <input class="form-control" th:field="*{nombre}"
          data-requerido="El nombre es obligatorio"
          data-solo-letras="El nombre solo debe contener letras"
          data-max-largo="100" data-msg-max-largo="El nombre admite máximo 100 caracteres" data-contador>
   ```
3. **Este documento**: actualizar la tabla del campo.

### Atributos disponibles

| Atributo | Efecto | Tipo |
|---|---|---|
| `data-validar` (en `<form>`) | Activa la validación en ese formulario | — |
| `data-requerido="msg"` | Campo obligatorio | Valida |
| `data-min-largo="n"` + `data-msg-largo="msg"` | Longitud mínima | Valida |
| `data-max-largo="n"` + `data-msg-max-largo="msg"` | Longitud máxima (si no hay mensaje: "Máximo n caracteres") | Bloquea |
| `data-contador` | Muestra "usados / máximo" bajo el campo (requiere `data-max-largo`) | Ayuda |
| `data-solo-numeros="msg"` | Solo dígitos 0–9; abre teclado numérico en celular | Bloquea |
| `data-solo-letras="msg"` | Solo letras (tildes, ñ), espacios, `.`, `-`, `'` | Bloquea |
| `data-max-digitos="n"` + `data-msg-digitos="msg"` | Cantidad máxima de dígitos | Bloquea |
| `data-min` / `data-max` + `data-msg-min` / `data-msg-max` | Rango numérico | Valida |
| `data-bloquear-rango="msg"` | No deja escribir valores fuera de `data-min`–`data-max` | Bloquea |
| `data-email="msg"` | Formato de correo | Valida |

Los campos numéricos se declaran como `type="text" inputmode="numeric"` (no `type="number"`), porque `type="number"` deja pasar `e`, `+`, `-` y `.` y no permite bloquearlos.

Los mensajes de conversión de tipo (cuando llega texto en un campo numérico al servidor) están en `src/main/resources/messages.properties`.

## 5. Historial de cambios de reglas

| Fecha | Cambio | Rama |
|---|---|---|
| 2026-11-10 | Validación en tiempo real en formularios de paciente y médico | `feature/validacion-tiempo-real` |
| 2026-11-10 | Documento: solo números | `feature/validacion-tiempo-real` |
| 2026-11-10 | Edad: máximo 3 dígitos; gravedad: solo 1 a 5 | `feature/validacion-tiempo-real` |
| 2026-11-10 | Nombres: máximo 100 caracteres, solo letras, contador; límite en todos los textos | `feature/validacion-tiempo-real` |
