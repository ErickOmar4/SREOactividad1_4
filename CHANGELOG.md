# Changelog

Todos los cambios relevantes de este proyecto se documentan en este archivo.

El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y el
proyecto usa [Versionado Semántico](https://semver.org/lang/es/).

## [1.0.0] - 2026-11-07

### Añadido
- Proyecto base Spring Boot 3.5.7 con la dependencia Spring Web (Maven, Java 17).
- `ProyectoController` con `GET /api/proyecto/nombre` (texto) y `GET /api/proyecto/estado` (JSON).
- `MateriaController` con `GET /api/materias` (lista) y `GET /api/materias/{id}` (objeto / 404).
- Capa de servicio `MateriaService` con datos simulados en memoria.
- Propiedades propias del proyecto mediante `@ConfigurationProperties` (`proyecto.*`).
- Puerto configurable (`server.port=${SERVER_PORT:8080}`) para el despliegue en el VPS.
- Pruebas automatizadas con JUnit 5 y MockMvc.
- Script `scripts/vps-run.sh` para iniciar, detener y consultar la app en el VPS con `nohup`.
- Documentación en `README.md` y `docs/`.
