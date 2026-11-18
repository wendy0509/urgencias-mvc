# ADR-0004: Migración de MVC a arquitectura hexagonal

- Estado: Aceptada
- Fecha: 2026-11-18
- Autores: Samuel López Marín, Wendy Stefania Infante Vera, Karoll Dayanna Moreno Mahecha

## Contexto

La actividad *Transformación de Arquitectura MVC a Arquitectura Hexagonal* pide reorganizar el proyecto MVC (ADR-0001) en dominio, puertos y adaptadores, conservando el CRUD de las dos entidades relacionadas, la base de datos y la interfaz web. ADR-0002 ya había elegido hexagonal como arquitectura objetivo.

## Decisión

1. Seguir la estructura de referencia de la guía: `domain/{model,port/in,port/out}`, `application/service`, `infrastructure/{adapter/in/web, adapter/out/persistence, config}`. Se agrega `domain/exception`.
2. Modelo de dominio en Java puro; entidades JPA (`*Entity`) y formularios (`*Form`) separados, con *mappers* en cada adaptador.
3. Un puerto de entrada por entidad (`Gestionar<Entidad>UseCase`) y un puerto de salida por entidad (`<Entidad>RepositoryPort`).
4. Servicios de aplicación sin anotaciones de Spring, registrados como beans en `BeanConfiguration`.
5. Conservar tablas, columnas, URL y plantillas para no romper datos ni la interfaz.
6. Verificar la regla de dependencia con ArchUnit y la funcionalidad con una prueba de integración sobre H2.

## Alternativas consideradas

- **Puertos en `application/port`** (como proponía ADR-0002): válido, pero se siguió la guía de la actividad.
- **Usar las entidades JPA como modelo de dominio**: menos clases, pero el dominio quedaría acoplado a JPA y la transformación no sería real.
- **Un puerto por operación** (`RegistrarPacienteUseCase`, `ListarPacientesUseCase`…): más granular, pero excesivo para un CRUD de dos entidades.

## Consecuencias

- (+) Dominio y casos de uso probables sin base de datos (19 pruebas unitarias).
- (+) Cambiar la persistencia o agregar canales (REST, WebSocket) solo afecta a infraestructura.
- (+) La prioridad pasa a ser un enum (deuda técnica #3 resuelta).
- (−) De 11 a 29 clases; tres representaciones por entidad y sus mapeos.
- (−) Las transacciones viven en los adaptadores de persistencia (cada operación del puerto es una transacción); la unicidad del documento la respalda además el índice `UNIQUE` de la tabla.
