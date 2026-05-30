# Funcionalidad Actual - Backend (precisionAppBE)

Este documento detalla la funcionalidad actual, reglas de negocio, modelos de datos y APIs expuestas del Backend desarrollado en **Spring Boot**, **MySQL** y **Flyway Migrations**.

---

## 1. Arquitectura y Stack Tecnológico
* **Framework**: Spring Boot 3.x (Java) con Gradle.
* **Persistencia**: Spring Data JPA con Hibernate, conectando a una base de datos MySQL.
* **Migraciones**: Flyway (`src/main/resources/db/migration/`) para versionado de base de datos.
* **Seguridad**: Autenticación basada en tokens JWT.
* **Documentación**: Swagger UI disponible en `/swagger-ui.html` y OpenAPI JSON en `/v3/api-docs`.

---

## 2. Modelos de Datos y Reglas de Negocio Clave

### 👥 Clientes
* **Tipos de Cliente**: `EMPRESA`, `ESTUDIANTE` y `COMUN` (cargados de tabla `tipo_cliente`).
* **Estado de Mora**: El backend calcula de manera lógica si un cliente está en mora basándose en las deudas de sus presupuestos vigentes.
* **Precio Minuto**:
  * Clientes `EMPRESA`: Tienen un precio de minuto personalizado almacenado en `precio_minuto_empresa`. Si no se especifica, utiliza el valor por defecto configurado en la tabla `varios`.
  * Clientes `ESTUDIANTE` / `COMUN`: Se les aplica el precio estándar por minuto (ajustado por el porcentaje de recargo si corresponde).

### 📝 Presupuestos y Trabajos
* **Estados del Presupuesto**: `Creado` ➡️ `Aprobado` ➡️ `Realizado` ➡️ `Cobrado` ➡️ `Entregado`.
* **Trabajos de Presupuesto**:
  * **Tipos de Trabajos**:
    * *Trabajos de Corte*: Se calcula multiplicando el `tiempoDeCorte` por el precio de minuto, sumando la fracción de superficie utilizada del material (`idSuperficie`).
    * *Trabajos Especiales* (`Grabado`, `Carteles`, `Cortes Especiales`): Tienen precios cargados de manera manual (`precioCorteManual`). `Grabado` requiere el campo `unidades` (mínimo 1) en lugar de fracciones.
  * **Lógica para Estudiantes**: Se calcula la cantidad de puntos de canje (ej. 1 punto cada 5 minutos). El descuento correspondiente se resta del precio del corte.
  * **Aprobación de Presupuesto**:
    * Al aprobar un presupuesto (`POST /api/presupuestos/aprobar/{id}`), el backend propone y busca el primer espacio libre disponible en el calendario de cada máquina asignada (`findFirstAvailableSlot`), respetando el horario de trabajo y sin solapar eventos.
    * Crea automáticamente un evento en el calendario para cada trabajo con el estado `PENDIENTE`.

### 🛍️ Ventas de Materiales
* **Venta Libre**: Permite vender cualquier material (tanto de corte como no de corte) especificando cantidad y precio.
* **Actualización de Stock**: Al realizar una venta, el backend resta del inventario la cantidad de material seleccionada.

### 💳 Pagos
* **Tipos de Pago**: `SENIA`, `PRESUPUESTO` y `VENTAS`.
* **Medios de Pago**: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA` y `MERCADO PAGO`.
* **Validación de Integridad**:
  * Si el medio es `TRANSFERENCIA`, el backend valida que `idCuentaBancaria` apunte a un registro válido en `cuentas_bancarias`.
  * Si el medio es `TARJETA`, valida que `idTarjeta` apunte a un registro válido en `tarjetas`.
  * Si el medio es `MERCADO PAGO`, dado que no existe FK directa en la tabla, el backend almacena la cuenta o titular en el campo `notas` (formato `MP: [titular]`).
  * Los pagos se pueden habilitar/deshabilitar de forma lógica. El backend actualiza automáticamente el saldo pendiente del presupuesto o venta.

### 🔒 Cierres de Caja
* **Apertura y Cierre**: Se registra el `montoInicial` al abrir el día y el `montoFinal` al cerrar caja.
* **Control de Duplicados**: El backend no permite crear más de un registro de cierre de caja por día.

---

## 3. Catálogo de APIs Clave

### 📂 Clientes
* `GET /api/clientes` (Lista paginada con filtros de nombre, tipo y mora).
* `POST /api/clientes` / `PUT /api/clientes/{id}` (Guardar/Editar).

### 📂 Presupuestos y Trabajos
* `GET /api/presupuestos/cliente/{clienteId}` (Presupuestos del cliente).
* `POST /api/presupuestos/aprobar/{id}` (Aprobar y agendar turnos de máquinas).
* `GET /api/trabajos/{presupuestoId}` (Trabajos de un presupuesto).
* `POST /api/trabajos` (Crear trabajo y calcular fórmulas).
* `PATCH /api/trabajos/{id}/seleccion_presupuesto` (Check para incluir en total).
* `PATCH /api/trabajos/{id}/estado` (Cambiar estado a `REALIZADO` o `ENTREGADO`).

### 📂 Pagos
* `GET /api/pagos/presupuesto/{id}` / `GET /api/pagos/venta/{id}` (Listar pagos).
* `POST /api/pagos` (Crear pago y actualizar saldos).
* `PUT /api/pagos/{id}` (Habilitar/Deshabilitar pago).

### 📂 Calendarios
* `GET /api/events` (Eventos filtrados por máquina y rango de fecha).
* `POST /api/events` / `PUT /api/events/{id}` / `DELETE /api/events/{id}` (CRUD de turnos).
