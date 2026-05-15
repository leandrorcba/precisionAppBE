# Seguridad — PrecisionApp Backend

## Índice

1. [Stack y dependencias](#1-stack-y-dependencias)
2. [Modelo de usuario y roles](#2-modelo-de-usuario-y-roles)
3. [Flujo general](#3-flujo-general)
4. [Generación y validación de JWT](#4-generación-y-validación-de-jwt)
5. [Filtro JWT](#5-filtro-jwt)
6. [Configuración de Spring Security](#6-configuración-de-spring-security)
7. [Autenticación: register y login](#7-autenticación-register-y-login)
8. [CORS](#8-cors)
9. [Endpoints públicos vs protegidos](#9-endpoints-públicos-vs-protegidos)
10. [Propiedades de configuración](#10-propiedades-de-configuración)
11. [Observaciones y limitaciones actuales](#11-observaciones-y-limitaciones-actuales)

---

## 1. Stack y dependencias

| Dependencia | Versión | Rol |
|---|---|---|
| `spring-boot-starter-security` | Spring Boot 4.0.3 | Core de seguridad |
| `jjwt-api` | 0.11.5 | API de JWT |
| `jjwt-impl` | 0.11.5 | Implementación JJWT |
| `jjwt-jackson` | 0.11.5 | Deserialización JSON de claims |

---

## 2. Modelo de usuario y roles

**Entidad:** `model/User.java`  
Implementa `UserDetails` de Spring Security directamente.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | PK |
| `username` | String | Nombre de usuario único |
| `password` | String | Hash BCrypt |
| `role` | Role (enum) | Rol del usuario |

**Roles disponibles** (`model/Role.java`):

```
USER → ROLE_USER
ADMIN → ROLE_ADMIN
SUPER_ADMIN → ROLE_SUPER_ADMIN
```

`getAuthorities()` retorna `SimpleGrantedAuthority("ROLE_" + role.name())`.

> Los métodos de estado de cuenta (`isAccountNonExpired`, `isAccountNonLocked`, `isCredentialsNonExpired`, `isEnabled`) retornan siempre `true`. No hay lógica de bloqueo o expiración de cuentas implementada.

---

## 3. Flujo general

```
┌─────────────────────────────────────────────────────────┐
│                       REGISTER / LOGIN                  │
│                                                         │
│  POST /api/auth/register  →  crea user, retorna JWT     │
│  POST /api/auth/login     →  valida credenciales, JWT   │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼ JWT en header: Authorization: Bearer <token>
┌─────────────────────────────────────────────────────────┐
│                  JwtAuthenticationFilter                │
│                  (OncePerRequestFilter)                 │
│                                                         │
│  1. Lee header Authorization                            │
│  2. Extrae JWT (después de "Bearer ")                   │
│  3. Parsea username del token                           │
│  4. Carga UserDetails desde BD                          │
│  5. Valida token (username + expiración)                │
│  6. Si válido → setea autenticación en SecurityContext  │
│  7. Continúa cadena de filtros                          │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                  SecurityFilterChain                    │
│                                                         │
│  Endpoint público?  →  permite                          │
│  Endpoint protegido + autenticado?  →  permite          │
│  Endpoint protegido + no autenticado?  →  403 Forbidden │
└─────────────────────────────────────────────────────────┘
```

---

## 4. Generación y validación de JWT

**Clase:** `security/JwtService.java`

### Generación (`generateToken`)

```java
// Claims del token generado:
{
  "sub": "nombreUsuario",        // subject
  "role": "ADMIN",               // rol sin prefijo ROLE_
  "iat": 1715000000,             // issued at
  "exp": 1715086400              // expira en 24 horas
}
```

- Algoritmo de firma: **HS256**
- Clave: decodificada desde Base64 (`jwt.secret` en properties)
- Expiración: **24 horas** desde emisión

### Validación (`isTokenValid`)

1. Extrae username del claim `sub`
2. Compara con el `username` del `UserDetails` recibido
3. Verifica que el token no haya expirado
4. Retorna `true` solo si ambas condiciones se cumplen

### Extracción de claims

| Método | Retorna |
|---|---|
| `extractUsername(token)` | String — claim `sub` |
| `extractExpiration(token)` | Date — claim `exp` |
| `extractClaim(token, resolver)` | T — claim genérico |
| `extractAllClaims(token)` | Claims — todos los claims |

---

## 5. Filtro JWT

**Clase:** `security/JwtAuthenticationFilter.java`  
Extiende `OncePerRequestFilter` — garantiza ejecución una sola vez por request.

### Lógica paso a paso

```
Request llega al filtro
│
├─ Header "Authorization" ausente o no empieza con "Bearer "?
│   └─ Pasar al siguiente filtro (sin autenticación)
│
└─ Extraer JWT del header [7:]
    ├─ Parsear username del token
    │   └─ Excepción → capturada silenciosamente, continúa sin autenticar
    │
    └─ Username extraído + no hay autenticación en SecurityContext?
        ├─ Cargar UserDetails desde BD (UserDetailsService)
        ├─ isTokenValid(token, userDetails)?
        │   ├─ SÍ → crear UsernamePasswordAuthenticationToken
        │   │        setear details del request
        │   │        guardar en SecurityContextHolder
        │   └─ NO → no setear nada
        │
        └─ Continuar cadena de filtros
```

> Las excepciones de parseo se capturan silenciosamente. Un token malformado o con firma inválida resulta en request sin autenticar, que luego el SecurityFilterChain rechaza con **403 Forbidden** (no 401).

---

## 6. Configuración de Spring Security

**Clases:** `security/SecurityConfig.java` y `security/ApplicationConfig.java`

### SecurityFilterChain

```
CSRF         → DESHABILITADO (API stateless, no necesario)
Sessions     → STATELESS (sin sesiones server-side)
CORS         → Delegado a CorsConfig (WebMvcConfigurer)
```

### Orden de filtros

```
... → JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter → ...
```

### Beans definidos en ApplicationConfig

| Bean | Implementación | Detalle |
|---|---|---|
| `UserDetailsService` | Lambda | Busca por username en BD, lanza `UsernameNotFoundException` si no existe |
| `AuthenticationProvider` | `DaoAuthenticationProvider` | Usa el UserDetailsService + BCrypt |
| `AuthenticationManager` | Desde `AuthenticationConfiguration` | Usado en el servicio de auth |
| `PasswordEncoder` | `BCryptPasswordEncoder` | Hashing de contraseñas |

---

## 7. Autenticación: register y login

**Controller:** `controller/AuthController.java`  
**Service:** `services/AuthenticationService.java`

### Register — `POST /api/auth/register`

```
Body: { username, password, role }
│
├─ ¿username ya existe?  →  error
├─ Encodear password con BCrypt
├─ Crear y persistir entidad User
├─ Generar JWT
└─ Retornar { token }
```

### Login — `POST /api/auth/login`

```
Body: { username, password }
│
├─ AuthenticationManager.authenticate(UsernamePasswordAuthenticationToken)
│   └─ Internamente: carga UserDetails, compara BCrypt
│   └─ Si falla → lanza excepción (BadCredentialsException)
├─ Cargar User desde BD
├─ Generar JWT
└─ Retornar { token }
```

**DTOs involucrados:**

| DTO | Campos |
|---|---|
| `AuthRequest` | `username`, `password`, `role` |
| `AuthResponse` | `token` |

---

## 8. CORS

**Clase:** `security/CorsConfig.java` (implementa `WebMvcConfigurer`)

| Configuración | Valor |
|---|---|
| Paths | `/**` (todos) |
| Allowed Origins | Propiedad `cors.allowed-origins` (default: `http://localhost:5173`) |
| Allowed Methods | GET, POST, PUT, PATCH, DELETE, OPTIONS |
| Allowed Headers | Authorization, Content-Type, Accept |
| Allow Credentials | `false` |

> El origen se lee desde properties/env, no está hardcodeado — cumple con la regla `PERMISSIVE_CORS` de FindSecBugs.

---

## 9. Endpoints públicos vs protegidos

### Públicos (sin JWT)

| Patrón | Motivo |
|---|---|
| `POST /api/auth/**` | Register y login |
| `GET /error` | Handler de errores de Spring |
| `/v3/api-docs/**` | OpenAPI spec |
| `/swagger-ui/**` | Swagger UI |
| `/swagger-ui.html` | Swagger UI |
| `OPTIONS /**` | CORS preflight |

### Protegidos (requieren JWT válido)

Todo lo demás, incluyendo:
- `/api/clientes/**`
- `/api/presupuestos/**`
- `/api/trabajos/**`
- `/api/pagos/**`
- `/api/materiales/**`
- `/api/users/**`
- etc.

---

## 10. Propiedades de configuración

En `application.properties`:

```properties
# JWT — usar variable de entorno en producción
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970

# CORS — origen permitido del frontend
cors.allowed-origins=http://localhost:5173
```

> En producción, `jwt.secret` debe ser una variable de entorno con una clave de al menos 256 bits (32 bytes), generada aleatoriamente.

---

## 11. Observaciones y limitaciones actuales

### Sin control de acceso por rol

Los roles se almacenan en el JWT pero **no se verifican en ningún endpoint**. No hay `@PreAuthorize`, `@Secured`, ni `hasRole()` en el SecurityFilterChain. Todos los usuarios autenticados acceden a todos los endpoints protegidos indistintamente.

### Respuesta de error: 403 en lugar de 401

Por diseño del filtro (captura silenciosa de excepciones), requests con token inválido o ausente reciben **403 Forbidden** en lugar del estándar **401 Unauthorized**.

### Sin refresh tokens

El JWT expira a las 24 horas. No hay mecanismo de refresh. El cliente debe re-autenticarse con credenciales.

### Sin bloqueo de cuentas

Los métodos de estado de `UserDetails` siempre retornan `true`. No es posible deshabilitar o bloquear una cuenta sin eliminarla de la BD.

### Un solo rol por usuario

`generateToken` usa `userDetails.getAuthorities().stream().findFirst()` — solo el primer authority entra al JWT. Si en el futuro un usuario tiene múltiples roles, solo uno quedará en el token.
