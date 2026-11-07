# Configuración

## 1. `pom.xml`

| Elemento | Valor | Para qué |
|---|---|---|
| `parent` | `spring-boot-starter-parent:3.5.7` | Versiones compatibles y configuración de plugins |
| `groupId` / `artifactId` | `com.example` / `act1-t4` | Identificación del artefacto |
| `version` | `1.0.0` | Versión (SemVer) |
| `java.version` | `17` | Nivel de Java para compilar |
| `finalName` | `${project.artifactId}` | El `.jar` se llama `act1-t4.jar` (sin versión) |
| `spring-boot-maven-plugin` | — | Crea el `.jar` ejecutable (fat jar) |

**Cambiar la versión de Java:** modifica `<java.version>` (por ejemplo `21`) y asegúrate de que el VPS tenga ese JDK o uno superior.

**Cambiar `groupId`:** si cambias `com.example` también debes renombrar los paquetes Java (`com/example/act1t4`) para que coincidan; no es obligatorio, pero es la convención.

## 2. `application.properties`

Ruta: `src/main/resources/application.properties`

| Propiedad | Valor por defecto | Descripción |
|---|---|---|
| `spring.application.name` | `act1-t4` | Nombre lógico de la aplicación |
| `server.port` | `${SERVER_PORT:8080}` | Puerto de Tomcat |
| `server.error.include-message` | `always` | Incluye el mensaje en los errores (útil en el 404 de materias) |
| `proyecto.nombre` | `INICIALESact1_t4` | Nombre mostrado en `/api/proyecto/nombre` |
| `proyecto.autor` | `Apellidos Nombre` | Autor mostrado en `/api/proyecto/estado` |
| `proyecto.descripcion` | texto | Descripción del proyecto |

## 3. Cambiar el puerto (3 formas)

| Forma | Comando | Cuándo usarla |
|---|---|---|
| Argumento | `java -jar act1-t4.jar --server.port=8081` | Despliegue en el VPS (recomendada) |
| Variable de entorno | `SERVER_PORT=8081 java -jar act1-t4.jar` | Servicios / contenedores |
| Archivo | Cambiar el valor por defecto en `application.properties` | Solo para pruebas locales |

Prioridad en Spring Boot: **argumento > variable de entorno > archivo**.

## 4. Propiedades propias (`ProyectoProperties`)

1. Se declaran en `application.properties` con el prefijo `proyecto.`.
2. La clase `ProyectoProperties` (con `@ConfigurationProperties(prefix = "proyecto")`) las recibe.
3. `@ConfigurationPropertiesScan` en `Act1T4Application` la registra.
4. Se inyecta por constructor donde se necesite.

Para añadir una propiedad nueva, por ejemplo `proyecto.curso`:

```properties
proyecto.curso=Desarrollo Web
```

```java
public record ProyectoProperties(String nombre, String autor, String descripcion, String curso) { }
```

## 5. Perfiles (opcional)

Para tener configuraciones distintas por entorno puedes crear `application-vps.properties`:

```properties
server.port=8081
```

y ejecutar con `java -jar act1-t4.jar --spring.profiles.active=vps`.

## 6. Variables de entorno del script `vps-run.sh`

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `PORT` | `8081` | Puerto asignado a tu instancia |
| `JAR` | `act1-t4.jar` | Nombre del archivo a ejecutar |
| `LOG_FILE` | `app.log` | Archivo de log |
| `PID_FILE` | `app.pid` | Archivo donde se guarda el PID del proceso |

## 7. Secretos

Este proyecto no usa credenciales. Si en el futuro las necesitas (contraseñas, tokens), **no las pongas en
`application.properties` versionado**: usa variables de entorno o un `application-local.properties`
(ya ignorado por `.gitignore`).
