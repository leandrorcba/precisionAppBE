# CLAUDE.md — PrecisionApp Backend

Instrucciones de desarrollo para Claude Code en este proyecto.

---

## Stack

- Java 21 · Spring Boot 4.x · Spring Data JPA · Flyway · Spring Security · JWT
- MySQL 8 · Lombok · SpringDoc/Swagger
- Build: Gradle con Checkstyle + SpotBugs + FindSecBugs

---

## Herramientas de análisis estático

El proyecto corre **Checkstyle** y **SpotBugs (FindSecBugs)** en cada build (`./gradlew check`).
Todo código generado debe pasar ambas herramientas sin errores ni warnings.

### Reglas Checkstyle activas (`config/checkstyle/checkstyle.xml`)

| Regla | Implicación práctica |
|---|---|
| `NeedBraces` | Siempre usar `{}` aunque el bloque sea de una sola línea |
| `AvoidStarImport` | Nunca usar `import foo.bar.*` — imports explícitos siempre |
| `UnusedImports` | No dejar imports que no se usen |
| `EmptyBlock` | No dejar bloques `{}` vacíos sin comentario |
| `LineLength` max=140 | Líneas de máximo 140 caracteres |

```java
// ✅ correcto
if (entity == null) {
    return null;
}

// ❌ falla NeedBraces
if (entity == null) return null;
```

### Reglas SpotBugs/FindSecBugs relevantes

| Finding | Qué evitar |
|---|---|
| `ENTITY_LEAK` | Nunca retornar `@Entity` directamente desde un controller — usar DTOs |
| `PERMISSIVE_CORS` | No usar `allowedOrigins("*")` — leer origen desde properties/env |
| `SQL_INJECTION` | No concatenar strings en queries — usar `@Query` con parámetros o Specifications |
| `HARD_CODE_PASSWORD` | No hardcodear secrets — usar variables de entorno |

---

## Convenciones de código

### Controllers
- Siempre devolver DTOs, nunca entidades `@Entity`
- El `@RequestBody` de PUT/POST debe ser un DTO, nunca la entidad directamente
- El service es responsable del mapeo DTO ↔ Entity

### DTOs
- Seguir el patrón existente: constructor desde entidad + `static toDTO()`:
  ```java
  public FooDTO(Foo entity) { ... }

  public static FooDTO toDTO(Foo entity) {
      if (entity == null) {
          return null;
      }
      return new FooDTO(entity);
  }
  ```
- Usar Lombok: `@Getter @Setter @AllArgsConstructor @NoArgsConstructor`

### Services
- En métodos de update: cargar la entidad existente desde el repo, mapear campos del DTO, guardar
- No hacer `repo.save(entityRecibidalDelController)` directamente

---

## Verificar antes de terminar

```bash
./gradlew check    # corre Checkstyle + SpotBugs
./gradlew bootRun  # levanta la app (también corre Flyway)
```

---

## Seguridad de Dependencias (Supply Chain)

### Regla de antigüedad
- No instalar ni recomendar dependencias publicadas hace menos de 7 días
- Verificar fecha en Maven Central antes de agregar nuevas dependencias

### Cómo verificar antigüedad
```bash
# Verificar fecha via Maven Central (HEAD request devuelve last-modified)
curl -I https://repo1.maven.org/maven2/{group/artifact/version}/{artifact}-{version}.pom

# Escanear vulnerabilidades conocidas
./gradlew dependencyCheckAnalyze
```

### Lockfile con verificación de hashes
```groovy
// build.gradle — activar dependency locking
dependencyLocking {
    lockAllConfigurations()
}
```
```bash
./gradlew dependencies --write-locks   # genera lockfile
./gradlew build                        # usa lockfile exacto
```

### Red flags — consultar antes de agregar
- Dependencia publicada hace menos de 7 días
- Group ID similar a uno conocido (typosquatting: `org.spring.boot` vs `org.springframework.boot`)
- Repositorio adicional en `build.gradle` que no sea `mavenCentral()`
- Dependencia con transferencia de propietario reciente

### Repositorios permitidos
Solo usar repositorios declarados actualmente:
```groovy
repositories {
    mavenCentral()  // único permitido
}
```
No agregar repositorios externos sin consultar.
