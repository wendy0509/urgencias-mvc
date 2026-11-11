# 06 · Manual del sistema

Guía para que cualquier persona instale, configure y ejecute la aplicación sin ayuda adicional del equipo.

## 1. Requisitos de instalación

| Programa | Versión | Descarga |
|---|---|---|
| JDK | 21 | <https://adoptium.net/> |
| XAMPP (MySQL/MariaDB) | 8.2.12 | <https://www.apachefriends.org/download.html> |
| IDE (opcional) | IntelliJ IDEA / VS Code / Spring Tool Suite | — |
| Git | 2.40+ | <https://git-scm.com/> |
| Navegador web | Chrome, Edge o Firefox actualizado | — |

Spring Boot **4.1.2** y Maven se descargan automáticamente con el wrapper (`mvnw`); no requieren instalación.

En Windows se pueden instalar desde PowerShell:
```powershell
winget install --id Git.Git -e
winget install --id EclipseAdoptium.Temurin.21.JDK -e
```

Cerrar la terminal, abrir una nueva y comprobar:
```bash
git --version
java -version    # debe mostrar 21
```

## 2. Configuración de la base de datos

| Parámetro | Valor |
|---|---|
| Motor | MySQL (XAMPP) |
| Nombre de la BD | `urgencias_db` |
| Puerto | **3306** (por defecto de XAMPP) |
| Usuario | `root` |
| Contraseña | *(vacía, valor por defecto de XAMPP)* |
| Creación de tablas | Automática (`spring.jpa.hibernate.ddl-auto=update`) |

**La base de datos debe crearse previamente en XAMPP (MySQL).**

> El proyecto usa el puerto por defecto de MySQL (3306). Si tu MySQL está en otro puerto, cámbialo en `spring.datasource.url` solo en tu copia local.

### 2.1 Crear la base de datos

En phpMyAdmin (<http://localhost/phpmyadmin>) → pestaña **SQL**:

```sql
CREATE DATABASE IF NOT EXISTS urgencias_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Las tablas `medicos` y `pacientes` las crea Hibernate al iniciar la aplicación. Script equivalente de referencia:

```sql
USE urgencias_db;

CREATE TABLE medicos (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(100) NOT NULL,
  especialidad  VARCHAR(80)  NOT NULL,
  correo        VARCHAR(100) NOT NULL,
  disponible    BIT(1)       NOT NULL DEFAULT 1
);

CREATE TABLE pacientes (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre           VARCHAR(100) NOT NULL,
  documento        VARCHAR(20)  NOT NULL UNIQUE,
  edad             INT          NOT NULL,
  motivo_consulta  VARCHAR(255) NOT NULL,
  nivel_gravedad   INT          NOT NULL,
  alergias         VARCHAR(255),
  prioridad        VARCHAR(255),
  fecha_llegada    DATETIME(6),
  estado           VARCHAR(20),
  medico_id        BIGINT NOT NULL,
  CONSTRAINT fk_paciente_medico FOREIGN KEY (medico_id) REFERENCES medicos(id)
);
```

### 2.2 `application.properties`

```properties
spring.application.name=urgencias
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/urgencias_db?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.thymeleaf.cache=false
```

Si tu MySQL tiene contraseña, agrega `spring.datasource.password=<tu_contraseña>` **solo en tu copia local** (no la subas al repositorio).

## 3. Ejecución

```bash
git clone https://github.com/wendy0509/urgencias-mvc.git
cd urgencias-mvc
git checkout develop

./mvnw spring-boot:run       # Linux / macOS
mvnw.cmd spring-boot:run     # Windows
```

En **PowerShell** el wrapper se escribe con `.\` delante: `.\mvnw.cmd spring-boot:run`.

Desde el IDE: abrir la carpeta como proyecto Maven y ejecutar `UrgenciasApplication.java`.

La primera ejecución tarda unos minutos porque Maven descarga Spring Boot y las librerías. Está lista cuando la consola muestra `Started UrgenciasApplication`.

La aplicación queda disponible en <http://localhost:8080/>.

## 4. URLs de acceso

| Funcionalidad | Método | URL |
|---|---|---|
| Página principal | GET | `http://localhost:8080/` |
| Listar pacientes (ordenados por gravedad y llegada) | GET | `http://localhost:8080/pacientes` |
| Formulario de nuevo paciente | GET | `http://localhost:8080/pacientes/nuevo` |
| Ver detalle de paciente | GET | `http://localhost:8080/pacientes/{id}` |
| Editar paciente | GET | `http://localhost:8080/pacientes/{id}/editar` |
| Guardar paciente (crear/actualizar) | POST | `http://localhost:8080/pacientes/guardar` |
| Eliminar paciente | POST | `http://localhost:8080/pacientes/{id}/eliminar` |
| Listar médicos | GET | `http://localhost:8080/medicos` |
| Formulario de nuevo médico | GET | `http://localhost:8080/medicos/nuevo` |
| Ver detalle de médico | GET | `http://localhost:8080/medicos/{id}` |
| Editar médico | GET | `http://localhost:8080/medicos/{id}/editar` |
| Guardar médico | POST | `http://localhost:8080/medicos/guardar` |
| Eliminar médico | POST | `http://localhost:8080/medicos/{id}/eliminar` |

Puerto: **8080** (configurable con `server.port`).

## 5. Guía de uso

> Registrar primero al menos un **médico**: todo paciente debe quedar asignado a uno.

### 5.1 Médicos
- **Registrar:** Médicos → *Nuevo* → nombre, especialidad, correo y disponibilidad → *Guardar*.
- **Consultar:** la lista muestra todos los médicos; *Ver* abre el detalle con sus pacientes.
- **Actualizar:** *Editar* → modificar → *Guardar*.
- **Eliminar:** *Eliminar*. No se permite si el médico tiene pacientes asignados (se muestra un mensaje).

### 5.2 Pacientes
- **Registrar:** Pacientes → *Nuevo* → nombre completo (máx. 100, solo letras), documento (5–20 dígitos, único), edad (0–120), motivo de consulta, nivel de gravedad (1–5), alergias, estado y médico → *Guardar*. La hora de llegada y la **prioridad** (BAJA, MEDIA, ALTA, CRITICA) se asignan automáticamente.
- **Consultar:** la lista se ordena por gravedad (mayor primero) y, a igual gravedad, por hora de llegada.
- **Actualizar:** *Editar*; la prioridad se recalcula y se conserva la hora de llegada original.
- **Eliminar:** *Eliminar* desde la lista.

### 5.3 Validaciones
Los formularios validan **mientras se escribe**: el error aparece en rojo bajo el campo y desaparece al corregirlo. Además:

- Los caracteres no permitidos **no entran**: letras en documento, edad o gravedad; números o símbolos en nombres y especialidad.
- Los textos no pueden pasar su máximo (por ejemplo, 100 caracteres en el nombre); un contador muestra cuántos se llevan.
- La edad admite máximo 3 dígitos (hasta 120) y la gravedad solo un número del 1 al 5.
- No se puede guardar mientras haya errores; el cursor va al primer campo con error.
- El servidor vuelve a validar todo (incluido el documento duplicado) aunque se desactive JavaScript.

Reglas completas, límites y mensajes de cada campo: [09-reglas-de-campos.md](09-reglas-de-campos.md).

*(Agregar capturas de pantalla en `docs/img/` y enlazarlas aquí antes de la entrega.)*

## 6. Solución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| `git` / `java` no se reconoce como comando | No está instalado o la terminal se abrió antes de instalarlo | Instalar (ver sección 1) y abrir una terminal nueva |
| `mvnw.cmd` no se reconoce en PowerShell | Falta `.\` | Usar `.\mvnw.cmd spring-boot:run` |
| `Communications link failure` | MySQL apagado o puerto distinto | Iniciar MySQL en XAMPP y verificar que escuche en el puerto 3306 |
| `Unknown database 'urgencias_db'` | BD no creada | Ejecutar el `CREATE DATABASE` del punto 2.1 |
| `Access denied for user 'root'` | MySQL tiene contraseña | Agregar `spring.datasource.password` localmente |
| `Port 8080 was already in use` | Otro proceso usa el puerto | Cerrarlo o cambiar `server.port` |
| Error de Lombok en el IDE (getters no encontrados) | *Annotation processing* desactivado | Activarlo en el IDE |
| `TemplateInputException: Error resolving template [pacientes/formulario]` | La plantilla se llamaba `fomulario.html` | Corregido en `feature/fix-plantilla-formulario-paciente`; actualizar la rama (`git pull`) |
| Los errores no aparecen mientras se escribe | El navegador guardó una versión vieja del JavaScript | Recargar con **Ctrl + F5** |
