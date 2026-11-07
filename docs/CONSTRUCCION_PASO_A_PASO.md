# Construcción paso a paso

Guía para **reproducir este proyecto desde cero**, indicando qué hace cada pieza, **dónde** se escribe y **por qué**.
Sirve también como apoyo mientras ves los videos de la playlist: ve programando al mismo tiempo.

| Paso | Qué haces |
|---|---|
| 1 | Instalar JDK, Maven, IDE y Git |
| 2 | Generar el proyecto en Spring Initializr |
| 3 | Verificar que arranca (servidor embebido) |
| 4 | Configurar `application.properties` |
| 5 | Crear los modelos |
| 6 | Crear el servicio |
| 7 | Crear el primer controlador (`ProyectoController`) |
| 8 | Crear el segundo controlador (`MateriaController`) |
| 9 | Probar los endpoints en local |
| 10 | Escribir las pruebas automatizadas |
| 11 | Empaquetar el `.jar` |
| 12 | Subir a GitHub |

---

## Paso 1 · Instalar el entorno

Instala lo indicado en el [README, sección 3](../README.md#3-requisitos-para-ejecutar-el-proyecto) y verifica:

```bash
java -version
mvn -version
git --version
```

## Paso 2 · Generar el proyecto (Spring Initializr)

Entra a <https://start.spring.io/> y completa:

| Campo | Valor |
|---|---|
| Project | **Maven** |
| Language | **Java** |
| Spring Boot | **3.5.7** (o la 3.5.x estable que aparezca) |
| Group | `com.example` |
| Artifact | `act1-t4` |
| Name | `act1-t4` |
| Packaging | **Jar** |
| Java | **17** |
| Dependencies | **Spring Web** |

Pulsa **Generate**, descomprime el `.zip` y ábrelo en tu IDE como proyecto Maven
(en IntelliJ: *File → Open* y selecciona `pom.xml`). El IDE descargará las dependencias.

> Alternativa: crear el proyecto desde el IDE (IntelliJ Ultimate: *New Project → Spring Boot*; Eclipse/STS: *New → Spring Starter Project*; VS Code: extensión *Spring Initializr*).

## Paso 3 · Verificar que corre

Ejecuta la clase `Act1T4Application` (o `mvn spring-boot:run`). Debes ver en consola:

```
Tomcat started on port 8080 (http) with context path '/'
Started Act1T4Application in 2.3 seconds
```

Si abres `http://localhost:8080` verás una página de error **404 (Whitelabel)**: es normal, todavía no existe ningún endpoint, pero demuestra que el servidor levantó.

## Paso 4 · Configurar `application.properties`

**Dónde:** `src/main/resources/application.properties`

```properties
spring.application.name=act1-t4
server.port=${SERVER_PORT:8080}
server.error.include-message=always

proyecto.nombre=TUSINICIALESact1_t4
proyecto.autor=Apellidos Nombre
proyecto.descripcion=API REST basica con Spring Boot - Actividad 1, Tema 4
```

**Clase que lee esas propiedades** — `src/main/java/com/example/act1t4/config/ProyectoProperties.java`:

```java
@ConfigurationProperties(prefix = "proyecto")
public record ProyectoProperties(String nombre, String autor, String descripcion) { }
```

**Registrarla** — en la clase principal agrega `@ConfigurationPropertiesScan`:

```java
@SpringBootApplication
@ConfigurationPropertiesScan
public class Act1T4Application { ... }
```

## Paso 5 · Crear los modelos

**Dónde:** `src/main/java/com/example/act1t4/model/`

`Materia.java`

```java
public record Materia(int id, String nombre, int creditos, int semestre) { }
```

`EstadoResponse.java`

```java
public record EstadoResponse(String estado, String proyecto, String autor,
                             String javaVersion, String puerto, OffsetDateTime fecha) { }
```

Un `record` es una clase inmutable que genera automáticamente constructor, accesores (`id()`, `nombre()`…), `equals`, `hashCode` y `toString`.

## Paso 6 · Crear el servicio

**Dónde:** `src/main/java/com/example/act1t4/service/MateriaService.java`

```java
@Service
public class MateriaService {

    private final List<Materia> materias = List.of(
            new Materia(1, "Programación Orientada a Objetos", 5, 3),
            new Materia(2, "Bases de Datos", 5, 4),
            new Materia(3, "Redes de Computadoras", 4, 4),
            new Materia(4, "Sistemas Operativos", 5, 5),
            new Materia(5, "Ingeniería de Software", 5, 6));

    public List<Materia> listar() { return materias; }

    public Optional<Materia> buscarPorId(int id) {
        return materias.stream().filter(m -> m.id() == id).findFirst();
    }
}
```

`@Service` le dice a Spring que cree y administre esta clase (un *bean*) para poder inyectarla.

## Paso 7 · Primer controlador: `ProyectoController`

**Dónde:** `src/main/java/com/example/act1t4/controller/ProyectoController.java`

```java
@RestController
@RequestMapping("/api/proyecto")
public class ProyectoController {

    private final ProyectoProperties propiedades;
    private final String puerto;

    public ProyectoController(ProyectoProperties propiedades,
                              @Value("${server.port}") String puerto) {
        this.propiedades = propiedades;
        this.puerto = puerto;
    }

    @GetMapping(value = "/nombre", produces = MediaType.TEXT_PLAIN_VALUE)
    public String nombre() {                       // Endpoint 1: texto
        return propiedades.nombre();
    }

    @GetMapping("/estado")
    public EstadoResponse estado() {               // Endpoint 2: objeto JSON
        return new EstadoResponse("OK", propiedades.nombre(), propiedades.autor(),
                System.getProperty("java.version"), puerto, OffsetDateTime.now());
    }
}
```

Qué hace cada anotación:

| Anotación | Significado |
|---|---|
| `@RestController` | Esta clase responde peticiones HTTP y devuelve datos (no vistas) |
| `@RequestMapping("/api/proyecto")` | Todas las rutas de la clase empiezan con ese prefijo |
| `@GetMapping("/nombre")` | Este método atiende `GET /api/proyecto/nombre` |
| `produces = TEXT_PLAIN_VALUE` | Responde `Content-Type: text/plain` |

## Paso 8 · Segundo controlador: `MateriaController`

**Dónde:** `src/main/java/com/example/act1t4/controller/MateriaController.java`

```java
@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaService servicio;

    public MateriaController(MateriaService servicio) {   // inyección por constructor
        this.servicio = servicio;
    }

    @GetMapping
    public List<Materia> listar() {                        // Endpoint 3: lista
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Materia obtener(@PathVariable("id") int id) {   // Endpoint 4: por id
        return servicio.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe la materia con id " + id));
    }
}
```

`@PathVariable` toma el valor de `{id}` en la URL. `ResponseStatusException` produce una respuesta HTTP 404 con mensaje.

## Paso 9 · Probar en local

Reinicia la aplicación y prueba:

| Qué | URL |
|---|---|
| Nombre | <http://localhost:8080/api/proyecto/nombre> |
| Estado | <http://localhost:8080/api/proyecto/estado> |
| Lista | <http://localhost:8080/api/materias> |
| Una materia | <http://localhost:8080/api/materias/2> |
| Error 404 | <http://localhost:8080/api/materias/99> |

Haz capturas de pantalla (navegador o Postman) de cada una: sirven como evidencia para la entrega.

## Paso 10 · Pruebas automatizadas

**Dónde:** `src/test/java/com/example/act1t4/controller/`. Se usa `@SpringBootTest` + `@AutoConfigureMockMvc`
para lanzar peticiones simuladas. Ver [`ProyectoControllerTest`](../src/test/java/com/example/act1t4/controller/ProyectoControllerTest.java)
y [`MateriaControllerTest`](../src/test/java/com/example/act1t4/controller/MateriaControllerTest.java).

```bash
mvn test
```

Resultado esperado: `Tests run: 6, Failures: 0, Errors: 0` y `BUILD SUCCESS`.

## Paso 11 · Empaquetar el `.jar`

```bash
mvn clean package
ls target/act1-t4.jar
java -jar target/act1-t4.jar        # prueba final del .jar en local
```

## Paso 12 · Subir a GitHub

1. Crea en GitHub un repositorio **vacío** llamado `TUSINICIALESact1_t4` (sin README ni `.gitignore`).
2. En la carpeta del proyecto:

```bash
git init
git add .
git status                      # verifica que NO aparezca target/
git commit -m "feat: proyecto base Spring Boot con 2 controladores y 4 endpoints"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/TUSINICIALESact1_t4.git
git push -u origin main
```

Continúa con el [despliegue en el VPS](DESPLIEGUE_VPS.md).

---

## Ejercicios de práctica (opcionales)

1. Añade `GET /api/proyecto/saludo?nombre=Ana` usando `@RequestParam` que responda `Hola, Ana`.
2. Añade el campo `docente` al `record Materia` y actualiza las pruebas.
3. Añade `GET /api/materias/semestre/{n}` que filtre por semestre.
4. Crea un tercer controlador con un endpoint que devuelva un número (por ejemplo, el total de créditos).
5. Cambia `ProyectoProperties` para incluir `proyecto.curso` y muéstralo en `/estado`.
6. Cambia el puerto con un perfil `vps` (`application-vps.properties`).
