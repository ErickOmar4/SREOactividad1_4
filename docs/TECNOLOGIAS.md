# Tecnologías

Guía ampliada de cada tecnología: qué es, para qué sirve, cómo se configuró y cómo se usa en este proyecto.

## Resumen de versiones

| Tecnología | Versión | Quién la define |
|---|---|---|
| Java (JDK) | 17 (compilación); funciona en 17+ | `java.version` en `pom.xml` |
| Spring Boot | 3.5.7 | `parent` en `pom.xml` |
| Spring Framework (Spring MVC) | 6.2.x | Gestionada por Spring Boot |
| Tomcat embebido | 10.1.x | Gestionada por Spring Boot |
| Jackson | 2.19.x | Gestionada por Spring Boot |
| JUnit Jupiter | 5.12.x | Gestionada por Spring Boot |
| Maven | 3.6.3+ (probado con 3.9.x) | Instalación local |

> Para ver las versiones exactas resueltas: `mvn dependency:tree`.

---

## Java / JDK

**Qué es.** Java es el lenguaje; el **JDK** (Java Development Kit) incluye el compilador (`javac`), la JVM (`java`) y las librerías estándar. El **JRE** solo sirve para ejecutar.

**Para qué sirve.** Compilar el código a *bytecode* y ejecutarlo en la JVM.

**Cómo se configuró.** En el `pom.xml`:

```xml
<properties>
  <java.version>17</java.version>
</properties>
```

Maven compila con `--release 17`, por lo que el `.jar` solo corre en Java 17 o superior. Por eso el VPS debe tener JDK/JRE 17+.

**Cómo se usa.** Los modelos usan `record`; las colecciones usan `List.of(...)` y `Stream`.

---

## Maven

**Qué es.** Herramienta de construcción basada en convenciones: define el ciclo *compile → test → package*.

**Para qué sirve.** Gestionar dependencias, compilar, probar y empaquetar.

**Cómo se configuró.** `pom.xml` con cuatro bloques:

| Bloque | Contenido |
|---|---|
| `<parent>` | `spring-boot-starter-parent`: versiones compatibles y plugins preconfigurados |
| `<properties>` | Versión de Java y codificación UTF-8 |
| `<dependencies>` | `spring-boot-starter-web` y `spring-boot-starter-test` (solo pruebas) |
| `<build>` | `finalName` y `spring-boot-maven-plugin` |

**Estructura estándar (convención sobre configuración):**

```
src/main/java        código de la aplicación
src/main/resources   application.properties y recursos
src/test/java        pruebas
target/              resultado de compilar (se genera; NO se sube a Git)
```

---

## Spring Boot

**Qué es.** Capa sobre Spring Framework que aporta *autoconfiguración*, *starters* y servidor embebido.

**Para qué sirve.** Evitar configuración manual (XML, servidor externo, versiones incompatibles).

**Cómo se usa.** La clase principal:

```java
@SpringBootApplication
@ConfigurationPropertiesScan
public class Act1T4Application {
    public static void main(String[] args) {
        SpringApplication.run(Act1T4Application.class, args);
    }
}
```

`@SpringBootApplication` combina tres anotaciones:

| Anotación | Efecto |
|---|---|
| `@Configuration` | La clase puede declarar *beans* |
| `@EnableAutoConfiguration` | Configura Tomcat, Jackson, MVC… según las dependencias presentes |
| `@ComponentScan` | Busca `@RestController`, `@Service`… en `com.example.act1t4` y subpaquetes |

> **Importante:** las clases deben estar en el mismo paquete o en subpaquetes de la clase principal, o Spring no las encontrará.

---

## Spring Web / Spring MVC

**Qué es.** Módulo web de Spring basado en el patrón **Front Controller**: el `DispatcherServlet` recibe todas las peticiones y las enruta al controlador correcto.

**Anotaciones usadas:**

| Anotación | Dónde | Función |
|---|---|---|
| `@RestController` | Clase | Marca la clase como controlador REST (`@Controller` + `@ResponseBody`) |
| `@RequestMapping("/api/materias")` | Clase | Prefijo común de las rutas |
| `@GetMapping("/{id}")` | Método | Atiende `GET` en esa ruta |
| `@PathVariable` | Parámetro | Toma un valor de la ruta (`/materias/2` → `id = 2`) |
| `@Value("${server.port}")` | Parámetro | Inyecta el valor de una propiedad |
| `@Service` | Clase | Marca un componente de lógica de negocio |

**`@Controller` vs `@RestController`.**

| | `@Controller` | `@RestController` |
|---|---|---|
| Uso típico | Aplicaciones con vistas HTML (Thymeleaf, JSP) | APIs REST |
| Retorno de los métodos | Nombre de una vista | Datos que se escriben en el cuerpo de la respuesta |
| Para devolver datos | Requiere `@ResponseBody` en cada método | Ya incluido |

---

## Tomcat embebido

**Qué es.** Servidor HTTP/Servlet incluido como librería dentro del `.jar`.

**Para qué sirve.** Escuchar en un puerto TCP y traducir HTTP a llamadas Java.

**Cómo se configuró.** `server.port=${SERVER_PORT:8080}`. La sintaxis `${VARIABLE:valor_por_defecto}` usa la variable de entorno si existe, o `8080` si no. También se puede sobrescribir con `--server.port=8081` al ejecutar.

**Orden de prioridad de Spring Boot (de mayor a menor):** argumentos de línea de comandos → variables de entorno → `application.properties`.

---

## Jackson

**Qué es.** Librería de serialización JSON. Viene con `spring-boot-starter-web`.

**Cómo se usa.** Sin código: al devolver un `record`, se convierte a JSON.

```java
public record Materia(int id, String nombre, int creditos, int semestre) { }
```

```json
{ "id": 1, "nombre": "Programación Orientada a Objetos", "creditos": 5, "semestre": 3 }
```

Las fechas (`OffsetDateTime`) se emiten en formato ISO-8601 (por ejemplo `2026-11-07T10:15:30.123-06:00`).

---

## `@ConfigurationProperties`

**Qué es.** Enlaza un grupo de propiedades con una clase tipada.

**Cómo se usa.**

```properties
proyecto.nombre=TUSINICIALESact1_t4
proyecto.autor=Apellidos Nombre
```

```java
@ConfigurationProperties(prefix = "proyecto")
public record ProyectoProperties(String nombre, String autor, String descripcion) { }
```

`@ConfigurationPropertiesScan` (en la clase principal) la registra como *bean*, y se inyecta por constructor en `ProyectoController`.

---

## JUnit 5, MockMvc y JsonPath

**Qué es.** JUnit 5 ejecuta las pruebas; MockMvc simula peticiones HTTP contra los controladores; JsonPath consulta el JSON de la respuesta.

**Cómo se usa.**

```java
@SpringBootTest
@AutoConfigureMockMvc
class MateriaControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void obtenerDevuelveLaMateriaSolicitada() throws Exception {
        mockMvc.perform(get("/api/materias/2"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(2));
    }
}
```

Se ejecutan con `mvn test` y también durante `mvn package`.

---

## Git y GitHub

- **Git:** control de versiones local. **GitHub:** repositorio remoto.
- El [`.gitignore`](../.gitignore) excluye `target/`, carpetas de IDE (`.idea/`, `.vscode/`, `.settings/`), logs, PIDs y secretos.
- [`.gitattributes`](../.gitattributes) normaliza finales de línea (LF) para que los scripts de shell funcionen en Linux aunque se editen en Windows.

---

## Herramientas de despliegue

| Herramienta | Función | Ejemplo |
|---|---|---|
| **SSH** | Sesión remota segura | `ssh usuario@IP` |
| **SCP / SFTP** | Copiar archivos al VPS | `scp target/act1-t4.jar usuario@IP:~/act1-t4/` |
| **UFW** | Firewall | `sudo ufw allow 8081/tcp` |
| **nohup** | Ignora la señal de cierre de sesión (SIGHUP) | `nohup java -jar act1-t4.jar &` |
| **ss / curl** | Verificar puertos y respuestas | `ss -tlnp \| grep 8081` |

Guía paso a paso: [`DESPLIEGUE_VPS.md`](DESPLIEGUE_VPS.md).
