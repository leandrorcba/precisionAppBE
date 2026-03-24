---
name: Seguir reglas Checkstyle y SpotBugs al escribir código Java
description: El proyecto usa Checkstyle + SpotBugs/FindSecBugs. Todo código generado debe cumplir las reglas activas.
type: feedback
---

Siempre seguir las reglas de Checkstyle y SpotBugs/FindSecBugs configuradas en el proyecto al escribir código Java.

**Why:** El build falla si no se cumplen (`ignoreFailures = false` en build.gradle). El usuario lo identificó como requisito explícito.

**How to apply:**
- `NeedBraces`: siempre usar `{}` en if/for/while aunque sea una sola línea
- `AvoidStarImport`: imports explícitos, nunca `import foo.*`
- `UnusedImports`: no dejar imports sin usar
- `EmptyBlock`: no dejar bloques vacíos sin comentario
- `LineLength` max=140: líneas de máximo 140 caracteres
- `ENTITY_LEAK` (SpotBugs): nunca retornar `@Entity` desde controllers, usar DTOs
- `PERMISSIVE_CORS` (SpotBugs): no usar `allowedOrigins("*")`
- Correr `./gradlew check` para verificar antes de dar por terminado un cambio