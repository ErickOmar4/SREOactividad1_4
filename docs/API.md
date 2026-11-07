# Referencia de la API

URL base (local): `http://localhost:8080` · URL base (VPS): `http://IP_DEL_VPS:TU_PUERTO`

Formato de las respuestas: texto plano (endpoint 1) y JSON UTF-8 (resto).

## Resumen

| # | Método | Ruta | Respuesta | Códigos |
|---|---|---|---|---|
| 1 | GET | `/api/proyecto/nombre` | `text/plain` | 200 |
| 2 | GET | `/api/proyecto/estado` | `application/json` | 200 |
| 3 | GET | `/api/materias` | `application/json` (lista) | 200 |
| 4 | GET | `/api/materias/{id}` | `application/json` (objeto) | 200, 404 |

---

## 1. `GET /api/proyecto/nombre`

Devuelve el nombre del proyecto; sirve para saber de quién es la instancia que responde en el VPS.

```bash
curl http://localhost:8080/api/proyecto/nombre
```

```
TUSINICIALESact1_t4
```

## 2. `GET /api/proyecto/estado`

```bash
curl http://localhost:8080/api/proyecto/estado
```

```json
{
  "estado": "OK",
  "proyecto": "TUSINICIALESact1_t4",
  "autor": "Apellidos Nombre",
  "javaVersion": "17.0.12",
  "puerto": "8080",
  "fecha": "2026-11-07T10:15:30.123-06:00"
}
```

| Campo | Descripción |
|---|---|
| `estado` | `"OK"` si la aplicación responde |
| `proyecto` / `autor` | Valores de `application.properties` |
| `javaVersion` | Versión de Java de la JVM que ejecuta la app |
| `puerto` | Valor configurado de `server.port` |
| `fecha` | Fecha y hora del servidor (ISO-8601) |

## 3. `GET /api/materias`

```bash
curl http://localhost:8080/api/materias
```

```json
[
  { "id": 1, "nombre": "Programación Orientada a Objetos", "creditos": 5, "semestre": 3 },
  { "id": 2, "nombre": "Bases de Datos", "creditos": 5, "semestre": 4 },
  { "id": 3, "nombre": "Redes de Computadoras", "creditos": 4, "semestre": 4 },
  { "id": 4, "nombre": "Sistemas Operativos", "creditos": 5, "semestre": 5 },
  { "id": 5, "nombre": "Ingeniería de Software", "creditos": 5, "semestre": 6 }
]
```

## 4. `GET /api/materias/{id}`

| Parámetro | Tipo | Ubicación | Descripción |
|---|---|---|---|
| `id` | entero | Ruta | Identificador de la materia |

```bash
curl http://localhost:8080/api/materias/2
```

```json
{ "id": 2, "nombre": "Bases de Datos", "creditos": 5, "semestre": 4 }
```

**Materia inexistente** (`/api/materias/99`) → `404 Not Found`:

```json
{
  "timestamp": "2026-11-07T16:15:30.123+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "No existe la materia con id 99",
  "path": "/api/materias/99"
}
```

**Id no numérico** (`/api/materias/abc`) → `400 Bad Request` (Spring no puede convertir el texto a entero).

---

## Códigos HTTP usados

| Código | Significado | Cuándo |
|---|---|---|
| 200 OK | Éxito | Respuestas normales |
| 400 Bad Request | Petición inválida | `id` no numérico |
| 404 Not Found | No existe el recurso | Materia inexistente o ruta mal escrita |

## Probar con Postman

1. Nueva petición → método **GET**.
2. URL: `http://localhost:8080/api/materias`.
3. **Send**. Revisa *Status* (200), *Headers* (`Content-Type`) y *Body* (JSON).
4. Repite para las demás rutas y, en el VPS, cambia el host por `http://IP_DEL_VPS:TU_PUERTO`.
