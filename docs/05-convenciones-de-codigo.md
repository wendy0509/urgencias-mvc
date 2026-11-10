# 05 · Convenciones de código

## 1. Idioma

- **Dominio en español**: clases, atributos, métodos y rutas del negocio (`Paciente`, `nivelGravedad`, `calcularPrioridad`, `/pacientes/nuevo`).
- **Términos técnicos del framework en inglés** como sufijo: `PacienteController`, `PacienteService`, `PacienteRepository`.
- Sin tildes ni `ñ` en identificadores de código (`anio`, no `año`). En textos visibles al usuario sí se usan.
- Documentación y mensajes al usuario: español.

## 2. Nombres en Java

| Elemento | Convención | Ejemplo |
|---|---|---|
| Paquete | minúsculas | `com.pascualbravo.urgencias.service` |
| Clase / interfaz / enum | PascalCase, sustantivo | `Paciente`, `ReglaDePrioridad` |
| Método | camelCase, verbo | `buscarPorId`, `calcularPrioridad` |
| Atributo / variable | camelCase | `fechaLlegada` |
| Constante | MAYUSCULAS_CON_GUION | `UMBRAL_ESPERA_MINUTOS` |
| Valor de enum | MAYUSCULAS_CON_GUION | `EN_ESPERA` |
| Prueba | `<Clase>Test`, método `debe<Comportamiento>` | `PacienteServiceTest.debeAsignarPrioridadCriticaAMenorDeCinco` |

## 3. Capas (etapa MVC)

| Paquete | Puede depender de | No debe |
|---|---|---|
| `controller` | `service`, `model` | Usar repositorios directamente |
| `service` | `repository`, `model` | Conocer `Model`, `HttpServletRequest` u objetos de vista |
| `repository` | `model` | Contener lógica de negocio |
| `model` | — | Depender de otras capas |

- Inyección por **constructor** (no `@Autowired` en atributos).
- Validaciones de entrada con Bean Validation en la entidad o DTO; reglas de negocio en el servicio.
- Mensajes de validación en español y claros para el usuario.

## 4. Vistas (Thymeleaf)

- Carpeta por entidad: `templates/<entidad>/lista.html`, `formulario.html`, `detalle.html`.
- El nombre del archivo debe coincidir **exactamente** con el `return` del controlador.
- Layout común en `templates/fragments/`.
- Sin lógica de negocio en las plantillas.

### Validación de campos

- Cada campo de texto tiene **máximo de caracteres**; cada campo numérico acepta **solo dígitos** y tiene rango.
- La regla se escribe en la entidad (`@NotBlank`, `@Size`, `@Pattern`, `@Min`, `@Max`, `@Email`) **y** en la plantilla con atributos `data-*`, con el **mismo mensaje**.
- Campos numéricos: `type="text" inputmode="numeric"`, no `type="number"`.
- Formularios con el atributo `data-validar` y el script `@{/js/validacion-formularios.js}` al final del `<body>`.
- Mensajes cortos, en español, que digan qué hacer: "El documento solo debe contener números".
- Toda regla nueva o modificada se registra en [09-reglas-de-campos.md](09-reglas-de-campos.md).

## 5. Base de datos

- Tablas en plural y `snake_case`: `pacientes`, `medicos`.
- Columnas en `snake_case` (Hibernate convierte `fechaLlegada` → `fecha_llegada`).
- Llaves foráneas: `<entidad>_id` (`medico_id`).
- No subir credenciales reales al repositorio. Para valores locales distintos, usar `application-local.properties` (ignorado por Git) o variables de entorno.

## 6. Mensajes de commit

Formato [Conventional Commits](https://www.conventionalcommits.org/es/):

```
<tipo>(<alcance opcional>): <descripción en imperativo, minúsculas, sin punto final>

[cuerpo opcional: qué y por qué]

[pie opcional: Refs #12]
```

| Tipo | Uso |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de error |
| `docs` | Solo documentación |
| `style` | Formato, sin cambio de lógica |
| `refactor` | Cambio de estructura sin cambiar comportamiento |
| `test` | Pruebas |
| `build` | `pom.xml`, dependencias |
| `ci` | Integración continua |
| `chore` | Tareas varias (ignore, configuración) |

Ejemplos:

```
feat(paciente): registrar signos vitales al ingreso
fix(vista): corregir nombre de plantilla formulario de pacientes
docs: agregar flujo de trabajo git
refactor(prioridad): extraer reglas a patrón strategy
```

## 7. Formato

- Indentación de 4 espacios en Java, 2 o 4 en HTML (consistente por archivo).
- Codificación UTF-8. Finales de línea LF (salvo `*.cmd`, que usa CRLF según `.gitattributes`).
- Una clase pública por archivo.
- Eliminar imports y código comentado sin uso antes del PR.
