# ADR-0002: Arquitectura hexagonal con eventos como arquitectura objetivo

- Estado: Aceptada
- Fecha: 2026-11-08
- Autores: Samuel López Marín, Wendy Stefania Infante Vera, Karoll Dayanna Moreno Mahecha

## Contexto

Los requisitos más críticos son la cola en tiempo real (RF03, RF04), el recálculo de prioridad en menos de 500 ms (RNF02), la concurrencia sin inconsistencias (RNF03), disponibilidad del 99.9 % (RNF01) y reglas configurables sin redespliegue (RF09, RNF06). El taller *Estilos Arquitectónicos* evaluó MVC, capas, monolito, hexagonal, microservicios, SOA, eventos y otros.

## Decisión

Adoptar como arquitectura objetivo **hexagonal (puertos y adaptadores) con comunicación basada en eventos**, desplegada en varias instancias sin estado:

1. **Eventos** (prioridad 1): resuelven el tiempo real y la concurrencia con bajo acoplamiento.
2. **Hexagonal** (prioridad 2): aísla las reglas clínicas de la tecnología; los eventos y el WebSocket son adaptadores de salida.
3. **Microservicios** (prioridad 3): evolución futura cuando crezcan la carga o las sedes.

## Alternativas consideradas

- **Microservicios desde el inicio**: alta complejidad operativa para un equipo de tres personas y una sola sede.
- **Mantener solo MVC**: no resuelve tiempo real ni aísla las reglas de clasificación.

## Consecuencias

- (+) Reglas de prioridad como estrategias en el dominio, probables sin infraestructura.
- (+) Se puede cambiar BD, broker o interfaz sin tocar el núcleo.
- (−) Más interfaces y mapeos (DTO ↔ dominio ↔ JPA); exige disciplina, vigilada con ArchUnit.
- (−) Consistencia eventual y un broker (RabbitMQ) adicional que operar.
- La migración desde MVC (ADR-0001) se hará de forma incremental (ver [roadmap](../07-roadmap.md)).
