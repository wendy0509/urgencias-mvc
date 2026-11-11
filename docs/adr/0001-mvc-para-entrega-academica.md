# ADR-0001: MVC en capas con Spring Boot y MySQL para la primera entrega

- Estado: Aceptada
- Fecha: 2026-11-08
- Autores: Samuel López Marín, Wendy Stefania Infante Vera, Karoll Dayanna Moreno Mahecha

## Contexto

La actividad *Desarrollo de una aplicación web utilizando Arquitectura MVC con Spring Boot* exige: patrón MVC, base de datos relacional (MySQL con XAMPP o SQL Server), CRUD completo de al menos dos entidades relacionadas, vistas con formularios y tablas, validaciones y un manual de instalación.

## Decisión

- Spring Boot con Spring Web MVC y Thymeleaf para las vistas.
- Spring Data JPA sobre **MySQL (XAMPP)** en el puerto 3306.
- Entidades `Medico` (1) — `Paciente` (N).
- Paquetes por capa técnica: `model`, `repository`, `service`, `controller`.
- Java 21, Lombok para reducir código repetitivo.

## Alternativas consideradas

- **SQL Server**: permitido por la actividad, pero XAMPP es más sencillo de instalar para todo el equipo.
- **Empezar directamente en hexagonal**: no cumple el objetivo didáctico de la actividad (demostrar MVC) y añade complejidad prematura.

## Consecuencias

- (+) Cumple la actividad con una estructura simple y conocida.
- (+) Base funcional sobre la cual iterar.
- (−) La lógica de prioridad queda en la capa de servicio, acoplada a JPA; se extraerá al dominio en la migración (ADR-0002).
- (−) Sin tiempo real: la cola se actualiza al recargar la página.
