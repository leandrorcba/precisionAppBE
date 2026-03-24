# MIGRACION.md — PrecisionApp (Struts Legacy)

> Documento generado el 2026-03-22. Fuente de verdad para la migración completa del sistema legacy Struts 1.x.
>
> **Stack legacy:** Apache Struts 1.3.10 · Java 1.6 · MySQL 5.1 · iText 5.3.4 · Joda-Time 2.7 · DisplayTag 1.2 · Maven

---

## Índice

1. [Pantallas / Vistas](#1-pantallas--vistas)
2. [Actions / Controladores](#2-actions--controladores)
3. [Entidades y Tablas](#3-entidades-y-tablas)
4. [Flujos de Negocio](#4-flujos-de-negocio)
5. [Reglas de Negocio](#5-reglas-de-negocio)
6. [Estado de Migración](#6-estado-de-migración)
7. [Base de Datos](#7-base-de-datos)
8. [Cambios Funcionales Planificados](#8-cambios-funcionales-planificados)
9. [Decisiones de Arquitectura](#9-decisiones-de-arquitectura)

---

## 1. Pantallas / Vistas

### 1.1 Estructura General

El front-end usa **frameset HTML** (técnica obsoleta): `principal.jsp` divide la pantalla en dos frames — menú lateral y área de contenido.

---

### 1.2 Navegación / Layout

#### `index.jsp`
- **Descripción:** Entry point. Redirige al forward `welcome` → `default.do` → `principal.jsp`.
- **Campos / Forms:** Ninguno.
- **Action:** `/default.do` (forward directo a `principal.jsp`)

#### `principal.jsp`
- **Descripción:** Frameset principal. Frame izquierdo = `menu.jsp`, frame derecho = contenido.
- **Campos / Forms:** Ninguno (solo estructura frameset).
- **Action:** N/A

#### `menu.jsp`
- **Descripción:** Menú lateral de navegación con links a todos los módulos del sistema.
- **Links:**
  - Clientes → `clientesABML.do?accion=listarClientes`
  - Presupuestos → `presupuestosABML.do?accion=listarPresupuestos`
  - Materiales → `parametrosABML.do?accion=listarMateriales`
  - Parámetros → `parametrosABML.do?accion=listarParametros`
  - Cierres → `cierresABML.do?accion=listarCierres`
  - Ventas → `ventasABML.do?accion=listarVentas`
  - Insumos → `insumosABML.do?...`
  - Scheduler (externo, enlace a aplicación de escritorio)
- **Action:** N/A

#### `bienvenidos.jsp`
- **Descripción:** Pantalla de bienvenida / splash screen. Sin funcionalidad.
- **Campos / Forms:** Ninguno.

#### `error.jsp`
- **Descripción:** Página genérica de error para excepciones no capturadas.
- **Campos / Forms:** Muestra mensaje de error.

---

### 1.3 Módulo Clientes

#### `listado_clientes.jsp`
- **Descripción:** Grilla con todos los clientes del sistema. Permite filtrar por nombre, ordenar, y acceder a ABM.
- **Campos mostrados:** Nombre, DNI, Teléfono, Email, Puntos acumulados, Tipo cliente, Moroso.
- **Formulario de filtro:** `filtrado` (nombre cliente), `ordenado` (criterio de orden).
- **Botones / Acciones:**
  - Nuevo cliente → `preInsertCliente`
  - Editar → `preEditCliente?clienteId=X`
  - Borrar → `borrarCliente?clienteId=X` (con validación de presupuestos)
  - Ver presupuestos → `listarPresupuestosCliente?idCliente=X`
  - Buscar → `listarClientesFiltrado`
- **Action:** `/clientesABML.do`

#### `listado_clientes_reload.jsp`
- **Descripción:** Página de recarga automática. Redirige a `listarClientes` tras operaciones.
- **Action:** `clientesABML.do?accion=listarClientes`

#### `abm_cliente.jsp`
- **Descripción:** Formulario para crear o editar un cliente.
- **Campos:** nombreCliente, emailCliente, dniCliente, telefonoCliente, mora (checkbox), tipoCliente (ESTUDIANTE/NORMAL/EMPRESA), precioMinutoEmpresa (solo si EMPRESA).
- **Action:** `/clientesABML.do` → `insertCliente` o `editCliente`

#### `edit_cliente.jsp`
- **Descripción:** Vista alternativa para editar datos específicos del cliente incluyendo gestión de socios.
- **Campos:** Mismos que `abm_cliente.jsp` + datos de socios asociados.
- **Action:** `/clientesABML.do` → `editCliente`

---

### 1.4 Módulo Presupuestos

#### `listado_presupuestos.jsp`
- **Descripción:** Lista global de todos los presupuestos del sistema con sus estados.
- **Campos mostrados:** ID, Fecha, Hora, Cliente, Precio cobrado, Precio sin canje, Puntos canjeados, Aprobado, Realizado, Cobrado, Entregado, Seña.
- **Botones / Acciones:** Ver trabajos, Aprobar cobro, Ver canje de puntos.
- **Action:** `/presupuestosABML.do`

#### `listado_presupuestos_cliente.jsp`
- **Descripción:** Presupuestos filtrados por un cliente específico. Vista principal de trabajo.
- **Campos mostrados:** Los mismos + columna de acciones según estado del presupuesto.
- **Botones:**
  - Ver trabajos → `trabajosABML.do?accion=listarTrabajosPresupuesto&PresupuestoId=X`
  - Marcar cobrado → `marcaPresupuestoCobrado`
  - Canje de puntos → `showCanjePuntos`
  - Agregar seña → `agregarSenia`
- **Action:** `/presupuestosABML.do`

#### `listado_presupuestos_reload.jsp`
- **Descripción:** Recarga automática hacia la lista de presupuestos.
- **Action:** `presupuestosABML.do?accion=listarPresupuestosCliente`

#### `listado_presupuestoClienteACobrar.jsp`
- **Descripción:** Vista especial de presupuesto listo para cobrar con detalle de canje de puntos.
- **Campos mostrados:** Detalle del presupuesto, puntos disponibles para canje, precio con y sin descuento.
- **Action:** `/presupuestosABML.do` → `marcaPresupuestoCobrado`

#### `abm_senia.jsp`
- **Descripción:** Formulario para registrar una seña (anticipo) sobre un presupuesto.
- **Campos:** monto de seña.
- **Action:** `/presupuestosABML.do` → `grabarSenia`

---

### 1.5 Módulo Trabajos

#### `listado_trabajos_presupuesto.jsp`
- **Descripción:** Pantalla central del flujo de trabajo. Lista los trabajos (ítems) de un presupuesto y permite agregar nuevos, calcular precios y confirmar el presupuesto.
- **Campos mostrados:** ID trabajo, Fecha, Hora, Material, Superficie usada, Precio material, Precio trabajo, Tiempo corte, Puntos por corte, Precio corte, Vinilo, Vectorizado, Diseño, Precio/minuto, Seleccionado.
- **Formulario de nuevo trabajo:**
  - archivoCad (nombre del archivo CAD)
  - idMaterial (dropdown de materiales)
  - materialUtilizado (fracción: 1/4, 1/2, 3/4, 1)
  - precioMaterial (calculado automáticamente)
  - tiempoDeCorte (minutos)
  - puntosPorCorte (puntos)
  - precioCorte (precio)
  - vinilo, vectorizado, disenio (costos adicionales)
  - fechaRealizacion
- **Panel de cálculo (BCalculo):** Muestra totales de trabajos seleccionados (precio sin descuento, puntos acumulados, puntos disponibles para canje, descuento aproximado, precio con descuento).
- **Botones:**
  - Agregar trabajo → `insertTrabajo`
  - Calcular precio material → `calcularPrecioMaterial`
  - Calcular presupuesto → `calcularPresupuesto`
  - Confirmar presupuesto → `marcarPresupuestoRealizadoNoCLiente`
  - Seleccionar/deseleccionar trabajos (checkboxes)
- **Action:** `/trabajosABML.do`

#### `listado_trabajos_presupuesto_reload.jsp`
- **Descripción:** Recarga automática de la lista de trabajos.
- **Action:** `trabajosABML.do?accion=listarTrabajosPresupuesto`

---

### 1.6 Módulo Ventas

#### `listado_ventas.jsp`
- **Descripción:** Lista de ventas de materiales (sin presupuesto). Incluye formulario para registrar nueva venta.
- **Campos mostrados:** ID venta, Fecha, Hora, Material, Superficie, Precio material, Cantidad, Precio venta.
- **Formulario nueva venta:**
  - idMaterial (dropdown)
  - materialUtilizado (fracción)
  - precioMaterial (calculado)
  - cantidad
  - precioVenta
- **Botones:**
  - Calcular precio → `calcularPrecioMaterial`
  - Insertar venta → `insertVenta`
- **Action:** `/ventasABML.do`

#### `listado_ventas_reload.jsp`
- **Descripción:** Recarga automática de ventas.
- **Action:** `ventasABML.do?accion=listarVentas`

---

### 1.7 Módulo Materiales / Parámetros

#### `listado_materiales.jsp`
- **Descripción:** Grilla de materiales disponibles con sus precios por superficie. Permite agregar materiales y superficies.
- **Campos mostrados:** Tipo material, Superficie (1/4, 1/2, 3/4, 1), Precio.
- **Formulario nuevo material:** tipoMaterial, superficie, precio.
- **Botones:** Agregar material, Agregar tamaño, Editar precio.
- **Action:** `/parametrosABML.do`

#### `listado_materiales_reload.jsp` / `listado_material_reload.jsp`
- **Descripción:** Páginas de recarga para el módulo de materiales.

#### `edit_material.jsp`
- **Descripción:** Formulario para editar el precio de un material/superficie.
- **Campos:** idPrecio (hidden), precio.
- **Action:** `/parametrosABML.do` → `editMaterial`

#### `abm_parametros.jsp`
- **Descripción:** Formulario de configuración del sistema.
- **Campos:**
  - precioMinuto (precio por minuto de máquina)
  - puntosPorMinuto (minutos necesarios para acumular 1 punto)
  - horaApertura / horaCierre (horario semana)
  - horaAperturaFdS / horaCierreFdS (horario fin de semana)
  - ajuste (factor de ajuste de precios)
  - clave (contraseña del sistema)
- **Action:** `/parametrosABML.do` → `insertCambios`

---

### 1.8 Módulo Cierres de Caja

#### `listado_cierre.jsp`
- **Descripción:** Lista de cierres de caja filtrados por mes de trabajo.
- **Campos mostrados:** Fecha, Monto inicio, Monto presupuestos, Monto ventas, Seña, Extracciones, Compra materiales, Monto final (aplicación), Arqueo, Diferencia, Responsable.
- **Selector de mes de trabajo.**
- **Botones:** Nuevo cierre, Ver/Editar cierre existente, Exportar a Excel.
- **Action:** `/cierresABML.do`

#### `crear_cierre_caja.jsp`
- **Descripción:** Formulario para crear un nuevo cierre de caja diario.
- **Campos:**
  - fechaCierre
  - montoInicioCaja
  - montoArqueo (conteo físico del dinero)
  - responsableCierre
  - Lista de extracciones del día
  - Lista de compras de materiales del día
- **Campos calculados automáticamente:** montoPresupuestos (sum de presupuestos cobrados), montoVentas (sum de ventas), montoExtracciones, montoCompraMateriales, montoFinalAplicacion, diferencia.
- **Botones:** Agregar extracción, Agregar compra material, Guardar cierre.
- **Action:** `/cierresABML.do`

#### `editar_cierre_caja.jsp`
- **Descripción:** Igual a `crear_cierre_caja.jsp` pero en modo edición de un cierre existente.
- **Action:** `/cierresABML.do` → `updatecierre`

#### `mostrar_cierre_caja.jsp`
- **Descripción:** Vista de sólo lectura de un cierre ya confirmado.
- **Action:** `/cierresABML.do` → `chequearCierre_mostrar`

#### `abm_extracciones.jsp`
- **Descripción:** Formulario para registrar una extracción de efectivo de la caja.
- **Campos:** fechaExtraccion, montoExtraccion, motivoExtraccion, responsableExtraccion.
- **Action:** `/cierresABML.do` → `insertExtraccion`

#### `listado_cierre_reload.jsp`
- **Descripción:** Recarga automática de cierres.

---

### 1.9 Módulo Insumos

#### `insumos.jsp`
- **Descripción:** Gestión de insumos/suministros del negocio.
- **Action:** (no mapeado en struts-config.xml actual — posible módulo en desarrollo)

---

### 1.10 Reportes

#### `listado_reportes.jsp`
- **Descripción:** Pantalla de acceso a reportes del sistema.
- **Action:** N/A (enlaza a reportes PDF generados por TablasPdf)

#### `reportes.jsp`
- **Descripción:** Vista auxiliar de reportes.

#### `scheduler.jsp`
- **Descripción:** Integración con un scheduler/planificador externo de máquinas (aplicación de escritorio separada).

---

## 2. Actions / Controladores

> Todos los Actions heredan de `DispatchAction`. El parámetro discriminador es `accion` en la URL.
> URL pattern: `*.do` → ActionServlet de Struts.

---

### 2.1 `DispatchClientes`
- **Path:** `/clientesABML`
- **Form:** `formClientes`
- **Clase:** `ar.com.lbr.struts.action.DispatchClientes`

| Acción | Descripción | Forward destino |
|---|---|---|
| `listarClientes` | Lista todos los clientes | `listado_clientes.jsp` |
| `listarClientesFiltrado` | Filtra clientes por nombre | `listado_clientes.jsp` |
| `preInsertCliente` | Prepara formulario de alta | `abm_cliente.jsp` |
| `insertCliente` | Guarda nuevo cliente + crea registro de puntos | `listado_clientes_reload.jsp` |
| `preEditCliente` | Carga datos del cliente para editar | `abm_cliente.jsp` |
| `editCliente` | Actualiza datos del cliente | `listado_clientes_reload.jsp` |
| `borrarCliente` | Elimina cliente (si no tiene presupuestos) | Redirect o error |

- **Excepciones manejadas:** `ClienteConPresupuestosException` → redirige a `listado_clientes.jsp`
- **UseCases:** `ClientesUC`, `GeneralMethods`
- **Lógica adicional:** Al insertar, crea automáticamente una carpeta de archivos del cliente en el filesystem (via `GeneralMethods.crearCarpetaClienteUC`)

---

### 2.2 `DispatchPresupuestos`
- **Path:** `/presupuestosABML`
- **Form:** `formPresupuestos`
- **Clase:** `ar.com.lbr.struts.action.DispatchPresupuestos`

| Acción | Descripción | Forward destino |
|---|---|---|
| `listarPresupuestos` | Lista todos los presupuestos | `listado_presupuestos.jsp` |
| `listarPresupuestosCliente` | Presupuestos de un cliente | `listado_presupuestos_cliente.jsp` |
| `insertPresupuesto` | Crea nuevo presupuesto para un cliente | `listado_presupuestos_reload.jsp` |
| `showCanjePuntos` | Muestra pantalla de canje de puntos | `listado_presupuestoClienteACobrar.jsp` |
| `marcaPresupuestoCobrado` | Marca presupuesto como cobrado + descuenta puntos canjeados | `listado_presupuestos_reload.jsp` |
| `agregarSenia` | Carga formulario de seña | `abm_senia.jsp` |
| `grabarSenia` | Graba el monto de seña | `listado_presupuestos_reload.jsp` |

- **Excepciones manejadas:** `PresupuestoYaCobradoException`, `PresupuestoNoRealizadoException`, `PresupuestoNoAprobadoException`
- **UseCases:** `PresupuestosUC`, `TrabajosUC`, `ClientesUC`, `ParametrosUC`
- **Lógica adicional:** Integración con Desktop API para abrir carpetas del cliente en el explorador de Windows. Al crear presupuesto, crea carpetas en filesystem.

---

### 2.3 `DispatchTrabajos`
- **Path:** `/trabajosABML`
- **Form:** `formTrabajos`
- **Clase:** `ar.com.lbr.struts.action.DispatchTrabajos`

| Acción | Descripción | Forward destino |
|---|---|---|
| `listarTrabajosPresupuesto` | Lista trabajos de un presupuesto + calcula totales | `listado_trabajos_presupuesto.jsp` |
| `insertTrabajo` | Agrega un trabajo al presupuesto | `listado_trabajos_presupuesto_reload.jsp` |
| `calcularPrecioMaterial` | Recalcula precio del material seleccionado | `listado_trabajos_presupuesto_reload.jsp` |
| `calcularPresupuesto` | Calcula totales del presupuesto (incluyendo puntos) | `listado_trabajos_presupuesto_reload.jsp` |
| `marcarPresupuestoRealizadoNoCLiente` | Confirma y cierra el presupuesto, genera PDF, acumula puntos | `listado_presupuestos_reload.jsp` |

- **Excepciones manejadas:** `NingunTrabajoSeleccionadoException`, `PresupuestoNoCancelableException`, `PresupuestoYaCanceladoException`, `PresupuestoNoRealizadoException`, `PresupuestoNoAprobadoException`, `PresupuestoYaCobrado`, `PresupuestoNoCobradoException`, `PresupuestoYaRealizadoException`, `PresupuestoYaConfirmadoException`, `ValoresNoCoincidenException`, `TrabajoNoseleccionadoException`
- **UseCases:** `TrabajosUC`, `ParametrosUC`, `ClientesUC`, `PresupuestosUC`
- **Lógica adicional:** Al confirmar el presupuesto genera un PDF con `TablasPdf`. Actualiza puntos acumulados del cliente.

---

### 2.4 `DispatchCierres`
- **Path:** `/cierresABML`
- **Form:** `formCierres`
- **Clase:** `ar.com.lbr.struts.action.DispatchCierres`

| Acción | Descripción | Forward destino |
|---|---|---|
| `listarCierres` | Lista cierres del mes seleccionado | `listado_cierre.jsp` |
| `validarCierre` | Verifica si existe cierre para la fecha | — |
| `chequearCierre_create` | Prepara datos para crear cierre | `crear_cierre_caja.jsp` |
| `chequearCierre_update` | Prepara datos para editar cierre | `editar_cierre_caja.jsp` |
| `chequearCierre_mostrar` | Prepara datos para ver cierre | `mostrar_cierre_caja.jsp` |
| `preinsertExtraccion` | Carga formulario de extracción | `abm_extracciones.jsp` |
| `insertExtraccion` | Graba extracción y vuelve al cierre | `crear_cierre_caja.jsp` |
| `insertCompraMaterial` | Graba compra de material | `crear_cierre_caja.jsp` |
| `insertcierre` | Graba nuevo cierre | `listado_cierre_reload.jsp` |
| `updatecierre` | Actualiza cierre existente | `listado_cierre_reload.jsp` |
| `exportarCierre` | Exporta cierre a Excel (Apache POI) | `listado_cierre.jsp` |

- **Excepciones manejadas:** `FechaNoValidaException`
- **UseCases:** `CierresUC`, `PresupuestosUC`

---

### 2.5 `DispatchParametros`
- **Path:** `/parametrosABML`
- **Form:** `formParametros`
- **Clase:** `ar.com.lbr.struts.action.DispatchParametros`

| Acción | Descripción | Forward destino |
|---|---|---|
| `listarMateriales` | Lista materiales con precios | `listado_materiales.jsp` |
| `agregarMaterial` | Agrega nuevo material o nuevo tamaño | `listado_materiales_reload.jsp` |
| `preEditMaterial` | Carga formulario de edición de precio | `edit_material.jsp` |
| `editMaterial` | Actualiza precio de material | `listado_material_reload.jsp` |
| `listarParametros` | Carga parámetros del sistema | `abm_parametros.jsp` |
| `insertCambios` | Guarda cambios de parámetros | `abm_parametros.jsp` |

- **Excepciones manejadas:** `MaterialYaCreadoException`, `IngresarSuperficieException`
- **UseCases:** `ParametrosUC`, `ClientesUC`

---

### 2.6 `DispatchVentas`
- **Path:** `/ventasABML`
- **Form:** `formVentas`
- **Clase:** `ar.com.lbr.struts.action.DispatchVentas`

| Acción | Descripción | Forward destino |
|---|---|---|
| `listarVentas` | Lista ventas del mes | `listado_ventas.jsp` |
| `calcularPrecioMaterial` | Calcula precio según material y fracción | `listado_ventas.jsp` |
| `insertVenta` | Registra nueva venta | `listado_ventas_reload.jsp` |

- **UseCases:** `VentasUC`, `TrabajosUC`

---

### 2.7 `DispatchInsumos`
- **Path:** (no mapeado en struts-config.xml actualmente)
- **Clase:** `ar.com.lbr.struts.action.DispatchInsumos`
- **Estado:** Módulo en desarrollo / sin mapeo activo.

---

## 3. Entidades y Tablas

### 3.1 `clientes`
| Campo | Tipo | Descripción |
|---|---|---|
| idCliente | INT PK AUTO | Identificador del cliente |
| nombreCliente | VARCHAR | Nombre completo |
| emailCliente | VARCHAR | Email |
| dniCliente | VARCHAR | DNI/documento |
| telefonoCliente | VARCHAR | Teléfono |
| fechaCreacion | VARCHAR | Fecha/hora de alta (formato `dd/MM/yyyy HH:mm:ss`) |
| mora | VARCHAR | Flag de morosidad (`SI`/`NO`) |
| tipoCliente | VARCHAR | Tipo: `ESTUDIANTE`, `NORMAL`, `EMPRESA` |
| precioMinutoEmpresa | DOUBLE | Precio/minuto especial para clientes EMPRESA |

**Relaciones:** 1:1 con `puntos`, 1:N con `presupuesto`

**Clase Java:** `BCliente`

---

### 3.2 `puntos`
| Campo | Tipo | Descripción |
|---|---|---|
| idPuntos | INT PK AUTO | Identificador |
| idCliente | INT FK | Referencia a clientes |
| puntosAcumulados | INT | Puntos disponibles actualmente |
| puntosAcumuladosHistorico | INT | Total histórico (nunca decrece) |
| fechaPrimerPunto | DATE | Fecha del primer punto acumulado |
| puntosDisponibles | INT | Puntos pendientes de acreditar |
| puntosAcumuladosPdf | INT | Puntos al momento de generación del PDF |

**Clase Java:** `BPunto`

---

### 3.3 `presupuesto`
| Campo | Tipo | Descripción |
|---|---|---|
| idPresupuesto | INT PK AUTO | Identificador |
| idCliente | INT FK | Referencia a clientes |
| fechaPresupuesto | VARCHAR | Fecha de creación |
| horaPresupuesto | VARCHAR | Hora de creación |
| precioCobrado | DOUBLE | Precio final acordado (con o sin descuento) |
| precioSinCanje | DOUBLE | Precio antes de aplicar canje de puntos |
| puntosDisponibleCanje | INT | Minutos canjeables calculados |
| puntosDisponibles | INT | Puntos que se acumularán al cobrar |
| puntosCanjeados | INT | Puntos efectivamente canjeados |
| maquina | INT | Número de máquina asignada |
| aprobado | VARCHAR | `S`/`N` — si el cliente aprobó el presupuesto |
| realizado | VARCHAR | `S`/`N` — si el trabajo fue ejecutado |
| cobrado | VARCHAR | `S`/`N` — si fue cobrado |
| entregado | VARCHAR | `S`/`N` — si fue entregado al cliente |
| precioMinuto | DOUBLE | Precio/minuto vigente al momento del presupuesto |
| senia | DOUBLE | Monto de anticipo/seña |

**Clase Java:** `BPresupuesto`

---

### 3.4 `trabajopresupuestado`
| Campo | Tipo | Descripción |
|---|---|---|
| idTrabajoPresupuestado | INT PK AUTO | Identificador |
| idPresupuesto | INT FK | Referencia a presupuesto |
| archivoCad | VARCHAR | Nombre del archivo CAD del trabajo |
| Material | INT FK | Referencia a materiales (idmateriales) |
| precioMaterial | DOUBLE | Precio del material calculado |
| precioTrabajo | DOUBLE | Precio total del trabajo |
| puntosPorCorte | INT | Puntos que genera este trabajo |
| tiempoDeCorte | INT | Tiempo de corte en minutos |
| precioCorte | INT | Precio del corte (tiempo × precio/minuto) |
| seleccionado | VARCHAR | `1`/`0` — si está incluido en el presupuesto |
| fechaRealizacion | VARCHAR | Fecha de realización |
| HoraRealizacion | VARCHAR | Hora de realización |
| vinilo | DOUBLE | Costo adicional de vinilo |
| vectorizado | DOUBLE | Costo adicional de vectorizado |
| disenio | DOUBLE | Costo adicional de diseño |
| precioMinuto | DOUBLE | Precio/minuto al momento del trabajo |

**Clase Java:** `BTrabajo`

---

### 3.5 `materiales`
| Campo | Tipo | Descripción |
|---|---|---|
| idmateriales | INT PK AUTO | Identificador |
| materiales | VARCHAR | Nombre del tipo de material |

**Relaciones:** 1:N con `preciomateriales`

**Clase Java:** `BMaterial` (campo `tipoMaterial`)

---

### 3.6 `preciomateriales`
| Campo | Tipo | Descripción |
|---|---|---|
| idprecioMateriales | INT PK AUTO | Identificador |
| idMateriales | INT FK | Referencia a materiales |
| superficie | VARCHAR | Fracción usada: `1/4`, `1/2`, `3/4`, `1` |
| precioMaterial | DOUBLE | Precio para esa fracción |

**Clase Java:** `BMaterial` (campo `superficie` + `precio`)

---

### 3.7 `varios` (Parámetros del sistema)
| Campo | Tipo | Descripción |
|---|---|---|
| idVarios | INT PK | Siempre 1 (registro único) |
| precioMinuto | DOUBLE | Precio base por minuto de máquina |
| puntosPorMinuto | INT | Minutos que equivalen a 1 punto |
| horaInicio | TIME | Hora apertura días de semana |
| horaCierre | TIME | Hora cierre días de semana |
| horaInicioFdS | TIME | Hora apertura fin de semana |
| horaCierreFdS | TIME | Hora cierre fin de semana |
| ajuste | DOUBLE | Factor de ajuste de precios |

**Clase Java:** `BParametros`

---

### 3.8 `cierre`
| Campo | Tipo | Descripción |
|---|---|---|
| idCierre | INT PK AUTO | Identificador |
| fechaCierre | DATE | Fecha del cierre |
| montoInicial | DOUBLE | Efectivo al inicio del día |
| montoPresupuestos | DOUBLE | Total cobrado por presupuestos |
| montoExtracciones | DOUBLE | Total extraído de caja |
| montoFinal | DOUBLE | Monto final calculado por sistema |
| arqueo | DOUBLE | Conteo físico de efectivo |
| diferencia | DOUBLE | `arqueo - montoFinal` |
| ventas | DOUBLE | Total de ventas directas |
| montoCompraMateriales | DOUBLE | Total compras de materiales |
| senia | DOUBLE | Total de señas cobradas |
| mesCierre | VARCHAR | Mes de trabajo (ej: `Marzo-2026`) |
| responsable | VARCHAR | Nombre del responsable del cierre |

**Clase Java:** `BDatosCierre`

---

### 3.9 `extracciones`
| Campo | Tipo | Descripción |
|---|---|---|
| idextraccion | INT PK AUTO | Identificador |
| fechaExtraccion | VARCHAR | Fecha de la extracción |
| montoExtraccion | DOUBLE | Monto extraído |
| motivoExtraccion | VARCHAR | Descripción/motivo |
| responsableExtraccion | VARCHAR | Quien realizó la extracción |
| mesExtraccion | VARCHAR | Mes de trabajo correspondiente |

**Clase Java:** `BExtracciones`

---

### 3.10 `compramateriales`
| Campo | Tipo | Descripción |
|---|---|---|
| idcompramateriales | INT PK AUTO | Identificador |
| material | VARCHAR | Nombre del material comprado |
| tipo | VARCHAR | Tipo de material |
| montounitario | DOUBLE | Precio unitario |
| cantidad | INT | Cantidad comprada |
| montototal | DOUBLE | Total (unitario × cantidad) |
| fechaCompra | DATE | Fecha de compra |
| HoraCompra | TIME | Hora de compra |
| mescompra | VARCHAR | Mes de trabajo |
| caja | VARCHAR | Tipo de caja (`Diaria` / otros) |

**Clase Java:** `BCompraMaterial`

---

### 3.11 `ventas`
| Campo | Tipo | Descripción |
|---|---|---|
| idVenta | INT PK AUTO | Identificador |
| fechaVenta | VARCHAR | Fecha |
| horaVenta | VARCHAR | Hora |
| material | VARCHAR | Nombre del material |
| superficie | VARCHAR | Fracción vendida |
| precioMaterial | DOUBLE | Precio del material |
| cantidad | INT | Cantidad |
| precioVenta | DOUBLE | Precio total de la venta |
| idMaterial | INT FK | Referencia a materiales |

**Clase Java:** `BVentas`

---

### 3.12 Clases auxiliares sin tabla propia

| Clase Java | Descripción |
|---|---|
| `BCalculo` | DTO de cálculo del presupuesto (solo en memoria) |
| `BPdfExport` | DTO para generación de PDF |
| `BMaquina` | Número de máquina asignada al presupuesto |
| `BMes` | Helper con mes y año para filtros |
| `BControlPuntos` | Control de movimientos de puntos |
| `BInsumo` | Insumos (módulo en desarrollo) |
| `BDetalleVenta` | Detalle de venta |

---

## 4. Flujos de Negocio

### 4.1 Alta de Cliente

```
1. Usuario hace clic en "Nuevo Cliente" → abm_cliente.jsp
2. Completa: nombre, DNI, teléfono, email, tipo (ESTUDIANTE/NORMAL/EMPRESA), mora
3. Si tipoCliente = EMPRESA, ingresa precioMinutoEmpresa personalizado
4. Al guardar:
   a. Se inserta registro en tabla "clientes"
   b. Se obtiene el idCliente generado
   c. Se crea registro en tabla "puntos" con puntosAcumulados = 0
   d. Se crea carpeta física en el filesystem (ruta de ConfigXTRAS)
5. Redirige a listado de clientes
```

### 4.2 Creación y Gestión de Presupuesto

```
1. Desde listado de clientes → botón "Ver presupuestos" del cliente
2. Se lista todos los presupuestos del cliente (listado_presupuestos_cliente.jsp)
3. Usuario crea nuevo presupuesto:
   - Se registra con estado: aprobado=N, realizado=N, cobrado=N, entregado=N
   - Se crea carpeta en filesystem para el presupuesto
4. Usuario hace clic en "Ver trabajos" del presupuesto
5. → listado_trabajos_presupuesto.jsp (ver flujo 4.3)
6. Una vez confirmado el presupuesto (realizado=S):
   - Usuario puede marcarlo como "cobrado" desde el listado
   - Se puede aplicar canje de puntos antes de cobrar (ver flujo 4.4)
```

### 4.3 Carga y Cálculo de Trabajos

```
1. Usuario está en listado_trabajos_presupuesto.jsp
2. Para agregar trabajo:
   a. Selecciona material (dropdown)
   b. Selecciona fracción de material usada (1/4, 1/2, 3/4, 1)
   c. Sistema calcula precio del material → query a preciomateriales
   d. Usuario ingresa: tiempo de corte, puntos por corte, precio corte, diseño, vectorizado, vinilo
   e. Confirma → INSERT en trabajopresupuestado
3. Cada trabajo tiene checkbox "seleccionado"
4. Al calcular presupuesto (con trabajos seleccionados):
   a. Suma tiempoDeCorte de todos los seleccionados
   b. Suma puntosPorCorte de todos los seleccionados
   c. Precio sin descuento = Σ(precioCorte + precioMaterial + disenio + vectorizado + vinilo)
   d. Puntos totales = puntosAcumulados del cliente + puntosPorCorte del presupuesto
   e. Si puntosDisponibles > 20: puntosDisponibles = puntosTotales / 5 (divisor entero)
   f. Descuento aproximado = puntosDisponibles × precioMinuto
5. Usuario confirma el presupuesto → marcarPresupuestoRealizadoNoCLiente
   a. Valida que el precio coincida
   b. Marca presupuesto como realizado=S
   c. Genera PDF con TablasPdf
   d. Acumula puntos al cliente
```

### 4.4 Canje de Puntos y Cobro

```
1. Usuario abre "Canje de puntos" desde listado_presupuestos_cliente.jsp
2. Sistema muestra:
   - Precio sin canje
   - Puntos disponibles para canje
   - Descuento aproximado en $
3. Usuario ingresa cuántos puntos quiere canjear (puntosCanjeados)
4. Al confirmar cobro (marcaPresupuestoCobrado):
   a. Valida: presupuesto debe estar realizado=S
   b. Valida: presupuesto NO debe estar ya cobrado
   c. Registra precioCobrado (con descuento aplicado)
   d. Marca cobrado=S
   e. Descuenta puntos canjeados de tabla "puntos"
   f. Acredita nuevos puntos generados por el presupuesto
```

### 4.5 Cierre de Caja Diario

```
1. Usuario abre módulo Cierres → filtra por mes de trabajo
2. Para crear cierre del día:
   a. Sistema verifica si ya existe cierre para esa fecha
   b. Si no existe: carga crear_cierre_caja.jsp con datos precalculados:
      - montoPresupuestos = sum(precioCobrado) de presupuestos cobrados en esa fecha
      - montoVentas = sum(ventas) del día
      - lista de extracciones del día
      - lista de compras de materiales del día
   c. Usuario ingresa: montoInicioCaja, montoArqueo, responsable
   d. Sistema calcula:
      - montoFinalAplicacion = montoInicioCaja + montoPresupuestos + montoVentas + senia - montoExtracciones - montoCompraMateriales
      - diferencia = montoArqueo - montoFinalAplicacion
3. Al guardar: INSERT en tabla cierre
4. Cierre puede editarse si no fue cerrado definitivamente
5. Exportar a Excel usando Apache POI
```

### 4.6 Mes de Trabajo (Regla especial de fechas)

```
El mes de trabajo NO coincide exactamente con el mes calendario:
- Si día de una transacción <= 10: pertenece al mes ANTERIOR
- Si día > 10: pertenece al mes ACTUAL

Ejemplo:
- 05/marzo → pertenece a "Febrero-2026"
- 15/marzo → pertenece a "Marzo-2026"
```

### 4.7 Venta Directa de Material

```
1. Usuario abre Ventas → selecciona material y fracción
2. Sistema calcula precio del material automáticamente
3. Ingresa cantidad y precio final
4. INSERT en tabla ventas
5. Las ventas se incluyen en el cierre de caja del día
```

### 4.8 Gestión de Materiales y Parámetros

```
1. Materiales: cada tipo de material tiene precios diferentes por fracción (1/4, 1/2, 3/4, 1)
2. Para agregar material: se inserta en "materiales" y luego se agregan superficies en "preciomateriales"
3. Parámetros del sistema (tabla "varios"):
   - Solo hay un registro (idVarios=1)
   - precioMinuto afecta el precio de todos los trabajos nuevos
   - El ajuste es un factor multiplicador global
```

---

## 5. Reglas de Negocio

### 5.1 Cálculo de Precio del Trabajo

```
precioCorte = tiempoDeCorte × precioMinuto
precioTrabajo = precioCorte + precioMaterial + disenio + vectorizado + vinilo

Precio total del presupuesto (trabajos seleccionados):
  precioSinDescuento = Σ(precioCorte + precioMaterial + disenio + vectorizado + vinilo)
  → Se aplica Math.ceil() al resultado final (redondeo hacia arriba)
```

### 5.2 Cálculo de Puntos

```
Acumulación:
  puntosNuevos = Σ(puntosPorCorte) de trabajos seleccionados del presupuesto

Disponibilidad para canje:
  puntosTotales = puntosAcumulados (de la tabla puntos) + puntosPorCorte (del presupuesto actual)

  SI puntosTotales > 20:
    puntosDisponiblesParaCanje = puntosTotales / 5  (división entera)
  SINO:
    puntosDisponiblesParaCanje = 0

Valor del canje:
  descuentoAproximado = puntosDisponiblesParaCanje × precioMinuto
```

### 5.3 Precio/Minuto por Tipo de Cliente

```
- NORMAL y ESTUDIANTE: usan precioMinuto de la tabla "varios" (global)
- EMPRESA: usa precioMinutoEmpresa del registro del cliente (personalizado)

El precioMinuto queda guardado en cada presupuesto y trabajo al momento de creación.
Si precioMinuto en un trabajo es 0.0, se usa el precioMinuto global de "varios".
```

### 5.4 Precio del Material

```
Lookup por (idMaterial, superficie):
  → SELECT precioMaterial FROM preciomateriales
    WHERE idMateriales = ? AND superficie = ?

Fracciones válidas: "1/4" (0.25), "1/2" (0.50), "3/4" (0.75), "1" (1.00)
```

### 5.5 Cálculo del Cierre de Caja

```
montoFinalAplicacion = montoInicioCaja
                     + montoPresupuestos
                     + montoVentas
                     + senia
                     - montoExtracciones
                     - montoCompraMateriales

diferencia = montoArqueo - montoFinalAplicacion

(Una diferencia negativa indica faltante de caja)
```

### 5.6 Mes de Trabajo

```
SI día del mes <= 10:
  mesTrabajo = nombre_del_mes(fecha.mes - 1) + "-" + fecha.año
SINO:
  mesTrabajo = nombre_del_mes(fecha.mes) + "-" + fecha.año

Nombres de mes en español: Enero, Febrero, Marzo, Abril, Mayo, Junio,
                            Julio, Agosto, Septiembre, Octubre, Noviembre, Diciembre
```

### 5.7 Validaciones de Estado del Presupuesto

| Condición | Excepción |
|---|---|
| Intentar cobrar un presupuesto ya cobrado | `PresupuestoYaCobradoException` |
| Intentar cobrar sin haber realizado | `PresupuestoNoRealizadoException` |
| Intentar confirmar sin estar aprobado | `PresupuestoNoAprobadoException` |
| Intentar cancelar un presupuesto no cancelable | `PresupuestoNoCancelableException` |
| Intentar cancelar ya cancelado | `PresupuestoYaCanceladoException` |
| Intentar confirmar ya realizado | `PresupuestoYaRealizadoException` |
| Intentar confirmar ya confirmado | `PresupuestoYaConfirmadoException` |
| Los valores ingresados no coinciden | `ValoresNoCoincidenException` |
| Precio pendiente de cobro | `PresupuestoNoCobradoException` |

### 5.8 Validaciones de Clientes

| Condición | Excepción |
|---|---|
| Borrar cliente que tiene presupuestos | `ClienteConPresupuestosException` |

### 5.9 Validaciones de Materiales

| Condición | Excepción |
|---|---|
| Crear material con nombre ya existente | `MaterialYaCreadoException` |
| Crear superficie sin ingresar el valor | `IngresarSuperficieException` |

### 5.10 Validaciones de Trabajos

| Condición | Excepción |
|---|---|
| Confirmar presupuesto sin seleccionar trabajos | `NingunTrabajoSeleccionadoException` |
| Confirmar sin seleccionar ningún trabajo (variante) | `TrabajoNoseleccionadoException` |

### 5.11 Validaciones de Cierres

| Condición | Excepción |
|---|---|
| Fecha inválida al crear/buscar cierre | `FechaNoValidaException` |

### 5.12 Horarios de Atención (en Parámetros)

```
Se almacenan horaInicio, horaCierre (semana) y horaInicioFdS, horaCierreFdS (fin de semana).
Usados para determinar si se trabaja en horario normal o especial.
No hay lógica de cobro diferencial por horario actualmente implementada en el código visible,
pero los datos están disponibles para uso futuro.
```

### 5.13 Factor de Ajuste

```
Tabla "varios" tiene campo "ajuste" (DOUBLE).
No hay lógica activa implementada que lo aplique en los cálculos visibles.
Posiblemente fue planeado para ajustes de inflación o cambio de precios.
```

### 5.14 Generación de PDF

```
Al confirmar un presupuesto (marcarPresupuestoRealizadoNoCLiente):
  - Se genera un PDF con: datos del cliente, datos del presupuesto, lista de trabajos,
    puntos acumulados, puntos disponibles para canje, descuento, resultados.
  - Archivo guardado en: path definido en ConfigXTRAS.properties
    (path.archivo.usuario en producción, path.archivo.usuario.dev en desarrollo)
  - Ruta Windows: ej: C:\archivos_precisionApp\clientes\{nombreCliente}\{idPresupuesto}\
```

### 5.15 Filesystem

```
Al crear un cliente: se crea carpeta física: {pathBase}\{nombreCliente}\
Al crear un presupuesto: se crean subcarpetas: {pathBase}\{nombreCliente}\{idPresupuesto}\
```

### 5.16 Compras de Material en Cierre

```
Al registrar una compra de material en el cierre, se clasifica como caja = "Diaria".
Las compras de tipo "Diaria" se incluyen en el monto del cierre de caja del día.
```

---

## 6. Estado de Migración

> Leyenda: ❌ Pendiente | ✅ Completado | 🔄 En progreso | ⚠️ Requiere análisis especial

### 6.1 Pantallas / Vistas

| # | Pantalla | Estado |
|---|---|---|
| 1 | index.jsp | ❌ Pendiente |
| 2 | principal.jsp (frameset) | ❌ Pendiente |
| 3 | menu.jsp | ❌ Pendiente |
| 4 | bienvenidos.jsp | ❌ Pendiente |
| 5 | error.jsp | ❌ Pendiente |
| 6 | listado_clientes.jsp | ❌ Pendiente |
| 7 | abm_cliente.jsp | ❌ Pendiente |
| 8 | edit_cliente.jsp | ❌ Pendiente |
| 9 | listado_presupuestos.jsp | ❌ Pendiente |
| 10 | listado_presupuestos_cliente.jsp | ❌ Pendiente |
| 11 | listado_presupuestoClienteACobrar.jsp | ❌ Pendiente |
| 12 | abm_senia.jsp | ❌ Pendiente |
| 13 | listado_trabajos_presupuesto.jsp | ❌ Pendiente |
| 14 | listado_ventas.jsp | ❌ Pendiente |
| 15 | listado_materiales.jsp | ❌ Pendiente |
| 16 | edit_material.jsp | ❌ Pendiente |
| 17 | abm_parametros.jsp | ❌ Pendiente |
| 18 | listado_cierre.jsp | ❌ Pendiente |
| 19 | crear_cierre_caja.jsp | ❌ Pendiente |
| 20 | editar_cierre_caja.jsp | ❌ Pendiente |
| 21 | mostrar_cierre_caja.jsp | ❌ Pendiente |
| 22 | abm_extracciones.jsp | ❌ Pendiente |
| 23 | listado_reportes.jsp | ❌ Pendiente |
| 24 | scheduler.jsp | ⚠️ Requiere análisis (integración externa) |

### 6.2 Actions / Controladores

| # | Action | Acciones | Estado |
|---|---|---|---|
| 1 | DispatchClientes | listarClientes, listarClientesFiltrado, preInsertCliente, insertCliente, preEditCliente, editCliente, borrarCliente | ❌ Pendiente |
| 2 | DispatchPresupuestos | listarPresupuestos, listarPresupuestosCliente, insertPresupuesto, showCanjePuntos, marcaPresupuestoCobrado, agregarSenia, grabarSenia | ❌ Pendiente |
| 3 | DispatchTrabajos | listarTrabajosPresupuesto, insertTrabajo, calcularPrecioMaterial, calcularPresupuesto, marcarPresupuestoRealizadoNoCLiente | ❌ Pendiente |
| 4 | DispatchCierres | listarCierres, chequearCierre_create/update/mostrar, insertExtraccion, insertCompraMaterial, insertcierre, updatecierre, exportarCierre | ❌ Pendiente |
| 5 | DispatchParametros | listarMateriales, agregarMaterial, preEditMaterial, editMaterial, listarParametros, insertCambios | ❌ Pendiente |
| 6 | DispatchVentas | listarVentas, calcularPrecioMaterial, insertVenta | ❌ Pendiente |
| 7 | DispatchInsumos | (sin mapeo activo) | ⚠️ Requiere análisis |

### 6.3 Entidades y Tablas

| # | Tabla | Estado |
|---|---|---|
| 1 | clientes | ❌ Pendiente |
| 2 | puntos | ❌ Pendiente |
| 3 | presupuesto | ❌ Pendiente |
| 4 | trabajopresupuestado | ❌ Pendiente |
| 5 | materiales | ❌ Pendiente |
| 6 | preciomateriales | ❌ Pendiente |
| 7 | varios | ❌ Pendiente |
| 8 | cierre | ❌ Pendiente |
| 9 | extracciones | ❌ Pendiente |
| 10 | compramateriales | ❌ Pendiente |
| 11 | ventas | ❌ Pendiente |

### 6.4 Flujos de Negocio

| # | Flujo | Estado |
|---|---|---|
| 1 | Alta de cliente | ❌ Pendiente |
| 2 | Creación y gestión de presupuesto | ❌ Pendiente |
| 3 | Carga y cálculo de trabajos | ❌ Pendiente |
| 4 | Canje de puntos y cobro | ❌ Pendiente |
| 5 | Cierre de caja diario | ❌ Pendiente |
| 6 | Mes de trabajo (regla de fechas) | ❌ Pendiente |
| 7 | Venta directa de material | ❌ Pendiente |
| 8 | Gestión de materiales y parámetros | ❌ Pendiente |

### 6.5 Reglas de Negocio

| # | Regla | Estado |
|---|---|---|
| 1 | Cálculo de precio del trabajo | ❌ Pendiente |
| 2 | Cálculo de puntos y canje | ❌ Pendiente |
| 3 | Precio/minuto por tipo de cliente (EMPRESA vs otros) | ❌ Pendiente |
| 4 | Precio del material por fracción | ❌ Pendiente |
| 5 | Cálculo del cierre de caja | ❌ Pendiente |
| 6 | Mes de trabajo (día <= 10 → mes anterior) | ❌ Pendiente |
| 7 | Validaciones de estado del presupuesto (máquina de estados) | ❌ Pendiente |
| 8 | Validaciones de cliente | ❌ Pendiente |
| 9 | Validaciones de materiales | ❌ Pendiente |
| 10 | Generación de PDF al confirmar presupuesto | ❌ Pendiente |
| 11 | Creación de carpetas en filesystem | ⚠️ Requiere análisis (filesystem local Windows) |
| 12 | Exportación de cierre a Excel | ❌ Pendiente |
| 13 | Factor de ajuste (campo en tabla "varios") | ⚠️ Requiere análisis (lógica no implementada actualmente) |
| 14 | Horarios de atención (semana vs fin de semana) | ⚠️ Requiere análisis (datos presentes, lógica no activa) |
| 15 | Clasificación compras de material como caja "Diaria" | ❌ Pendiente |

---

## Notas Técnicas para la Migración

### Deuda Técnica Identificada

1. **SQL Injection:** Múltiples queries en UseCases usan concatenación de strings directa (sin `PreparedStatement`) — ej: `listarClientesFiltradoUC`, `listarTrabajoPresupuestadoFiltradoUC`. **Prioridad alta para corregir en la migración.**

2. **Frameset HTML:** La arquitectura de frames es obsoleta. En la migración usar SPA o navegación estándar.

3. **Static methods en UseCases:** Todos los métodos de acceso a datos son `static`, lo que hace difícil el testing. En la migración usar inyección de dependencias.

4. **Conexiones JDBC manuales:** No hay ORM. Todas las conexiones se manejan manualmente con `JDatos`. Migrar a JPA/Hibernate o Spring Data.

5. **Paths hardcodeados a Windows:** `ConfigXTRAS.properties` tiene paths absolutos de Windows. Requiere abstracción en la migración.

6. **Sin autenticación activa:** Existe `UserLoginForm.java` pero no hay mapeo activo de login en `struts-config.xml`. El sistema no tiene autenticación funcionando.

7. **Módulo Insumos incompleto:** `DispatchInsumos` y `InsumosUC` existen pero no están mapeados en `struts-config.xml`.

8. **Joda-Time:** Reemplazar por `java.time` (Java 8+) en la migración.

### Dependencias Externas

| Dependencia | Versión | Propósito | Reemplazar con |
|---|---|---|---|
| Apache Struts | 1.3.10 | MVC Framework | Spring MVC / Spring Boot |
| iText | 5.3.4 | Generación PDF | iText 7 / OpenPDF |
| Joda-Time | 2.7 | Fechas | java.time (Java 8+) |
| MySQL Connector | 5.1.49 | JDBC Driver | MySQL Connector 8.x |
| DisplayTag | 1.2 | Grillas en JSP | Componente frontend moderno |
| Apache POI | (sin versión explícita) | Excel export | Apache POI 5.x |
| Log4j | — | Logging | SLF4J + Logback |

---

## 10. Hoja de Ruta de Migración

> **Estado general al 2026-03-22:**
> Backend ≈ 95% completo · Frontend ≈ 70% completo
>
> Leyenda de impacto: **BE** = solo backend · **FE** = solo frontend · **BE+FE** = ambos
> Complejidad: 🟢 Baja · 🟡 Media · 🔴 Alta

---

### Prioridad 1 — Base (bloqueante para todo lo demás)

#### 1.1 Endpoints faltantes en CierresController

**Qué hacer:** `CierresController` solo tiene `GET`. Faltan `POST`, `PUT` y `DELETE` para crear, editar y eliminar cierres de caja.

| Endpoint | Descripción |
|---|---|
| `POST /api/cierres` | Crear nuevo cierre con todos sus montos |
| `PUT /api/cierres/{id}` | Actualizar cierre existente |
| `DELETE /api/cierres/{id}` | Eliminar cierre |
| `GET /api/cierres/{id}` | Obtener cierre por ID (para edición) |

- **Impacto:** BE
- **Depende de:** Nada
- **Bloquea:** 2.2 (Página Cierres)
- **Complejidad:** 🟢 Baja — `CierreService` ya tiene la lógica, solo faltan los métodos en el controller y service

---

#### 1.2 Endpoints faltantes en PagosController

**Qué hacer:** `PagosController` solo tiene `GET /api/presupuestos/{id}/pagos`. Faltan los endpoints para registrar pagos (señas, cobros con medio de pago).

| Endpoint | Descripción |
|---|---|
| `POST /api/presupuestos/{id}/pagos` | Registrar pago de presupuesto (efectivo, tarjeta, transferencia, seña) |
| `DELETE /api/presupuestos/{id}/pagos/{idPago}` | Eliminar pago |
| `POST /api/ventas/{id}/pagos` | Registrar pago de venta |

- **Impacto:** BE
- **Depende de:** Nada
- **Bloquea:** 2.3 (flujo de cobro con medio de pago)
- **Complejidad:** 🟡 Media — requiere validar reglas de negocio (monto total, estado del presupuesto)

---

#### 1.3 Endpoints faltantes para Compramateriales

**Qué hacer:** No existe controller ni endpoints para gestionar compras de materiales dentro de un cierre. La tabla `compramateriales` no tiene ningún endpoint en el BE.

| Endpoint | Descripción |
|---|---|
| `GET /api/compramateriales` | Listar compras (filtro por fecha/mes) |
| `POST /api/compramateriales` | Registrar compra de material |
| `DELETE /api/compramateriales/{id}` | Eliminar compra |

- **Impacto:** BE
- **Depende de:** Nada
- **Bloquea:** 2.2 (Página Cierres — el cierre necesita mostrar y gestionar compras)
- **Complejidad:** 🟢 Baja

---

#### 1.4 Endpoint de Puntos del cliente

**Qué hacer:** No existe endpoint para consultar ni actualizar los puntos de un cliente. Es necesario para mostrar puntos en el flujo de cobro y para el canje.

| Endpoint | Descripción |
|---|---|
| `GET /api/clientes/{id}/puntos` | Obtener puntos acumulados del cliente |
| `PUT /api/clientes/{id}/puntos` | Actualizar puntos (post-cobro, post-canje) |

- **Impacto:** BE
- **Depende de:** Nada
- **Bloquea:** 2.3 (flujo de canje de puntos)
- **Complejidad:** 🟢 Baja — entidad `Punto` ya existe, solo falta el endpoint

---

#### 1.5 Completar presupuestosService.js en el FE

**Qué hacer:** `presupuestosService.js` solo tiene `getPresupuestosByCliente` y `createPresupuesto`. Faltan:

| Método | Endpoint BE | Estado BE |
|---|---|---|
| `updatePresupuesto(dto)` | `POST /api/presupuestos/update` | ✅ Existe |
| `confirmarPresupuesto(id)` | `PATCH /api/trabajos/{id}/confirmar_presupuesto` | ✅ Existe |
| `getPagosByPresupuesto(id)` | `GET /api/presupuestos/{id}/pagos` | ✅ Existe |
| `getPresupuestoById(id)` | `GET /api/presupuestos/{id}` | ✅ Existe |

- **Impacto:** FE
- **Depende de:** Nada (los endpoints BE ya existen)
- **Bloquea:** 2.3 (flujo de cobro), 2.4 (confirmar presupuesto)
- **Complejidad:** 🟢 Baja — agregar métodos al archivo existente

---

#### 1.6 Completar utilsService.js en el FE

**Qué hacer:** `utilsService.js` solo tiene `getTipoCliente` y `getSuperficies`. Faltan:

| Método | Endpoint BE | Necesario para |
|---|---|---|
| `getTipoPago()` | `GET /api/utils/tipo-pago` | Flujo de cobro (medio de pago) |
| `getMedioPago()` | pendiente en BE | Flujo de cobro |
| `getAniosCierre()` | `GET /api/utils/anio-cierre` | Página Cierres (filtro por año) |

- **Impacto:** FE
- **Depende de:** Nada
- **Bloquea:** 2.2 (Cierres), 2.3 (Cobro)
- **Complejidad:** 🟢 Baja

---

### Prioridad 2 — Funcionalidades core del negocio

#### 2.1 Página Ventas (`/ventas`)

**Qué hacer:** Implementar `src/pages/ventas/Ventas.jsx` — actualmente es `Placeholder`. El backend (`VentasController` + `VentaService`) está **100% completo**.

**Funcionalidad a implementar:**
- Tabla de ventas con filtros por fecha (hoy / rango)
- Formulario de nueva venta: material, superficie, cantidad → calcula precio automáticamente
- Editar y eliminar venta
- Registrar pago de venta (medio de pago) — depende de ítem 1.2

**Servicios necesarios:**
- Crear `ventasService.js` (no existe)

- **Impacto:** FE (BE listo)
- **Depende de:** 1.6 (utilsService para superficies/tipo pago)
- **Bloquea:** Nada
- **Complejidad:** 🟡 Media — UI similar a Trabajos (formulario + tabla)

---

#### 2.2 Página Cierres (`/cierres`)

**Qué hacer:** Implementar `src/pages/cierres/Cierres.jsx` — actualmente es `Placeholder`. Es el módulo más complejo del sistema.

**Funcionalidad a implementar:**
- Filtro por mes/año de cierre
- Tabla de cierres del mes con todos los montos
- Crear cierre: ingresar monto inicio + arqueo, ver totales calculados del día
- Editar cierre existente
- Ver cierre (solo lectura)
- Subpanel: extracciones del día (listar, agregar, eliminar)
- Subpanel: compras de material del día (listar, agregar, eliminar)
- Cálculo automático de `montoFinal` y `diferencia`
- Exportar a Excel

**Servicios necesarios:**
- Crear `cierresService.js`
- Crear `extraccionesService.js`
- Crear `compramaterialesService.js`

- **Impacto:** BE+FE
- **Depende de:** 1.1 (Cierres CRUD BE), 1.3 (Compramateriales BE), 1.6 (utils años)
- **Bloquea:** Nada
- **Complejidad:** 🔴 Alta — múltiples subpaneles, cálculos en tiempo real, mayor pantalla del sistema

---

#### 2.3 Flujo de Cobro con Medio de Pago

**Qué hacer:** Implementar el flujo completo de cobrar un presupuesto desde la página de Presupuestos.

**Funcionalidad a implementar:**
- Botón "Cobrar" en la tabla de presupuestos (actualmente no existe)
- Dialog de cobro:
  - Muestra precio sin descuento
  - Selector de tipo de descuento (puntos / efectivo / ninguno)
  - Si puntos: muestra disponibles y permite ingresar a canjear
  - Selector de medio de pago (efectivo, tarjeta débito, tarjeta crédito, transferencia)
  - Si tarjeta: selector de tarjeta + cuotas
  - Si transferencia: selector de cuenta bancaria
  - Monto final calculado
- POST al endpoint de pagos (ítem 1.2)
- Actualiza estado `cobrado = 1` en presupuesto

- **Impacto:** BE+FE
- **Depende de:** 1.2 (Pagos POST BE), 1.4 (Puntos BE), 1.5 (presupuestosService), 1.6 (utils)
- **Bloquea:** Nada
- **Complejidad:** 🔴 Alta — flujo más complejo del sistema, múltiples caminos según tipo de pago

---

#### 2.4 Flujo Completo de Confirmación de Presupuesto

**Qué hacer:** El endpoint `PATCH /api/trabajos/{id}/confirmar_presupuesto` existe en BE pero la UI de Trabajos no tiene el botón de confirmar conectado al flujo completo.

**Funcionalidad a implementar:**
- Botón "Confirmar Presupuesto" en la página de Trabajos con validación de trabajos seleccionados
- Al confirmar: muestra resumen (precio total, puntos a ganar, precio con/sin descuento)
- El usuario ingresa el precio acordado
- POST confirmar → marca `aprobado = 1`
- La página de trabajos debe bloquearse una vez aprobado el presupuesto (ya hay lógica parcial)
- Acumula puntos en el cliente (depende de ítem 1.4)

- **Impacto:** FE (+BE para acumulación de puntos)
- **Depende de:** 1.4 (Puntos), 1.5 (presupuestosService)
- **Bloquea:** 2.3 (Cobro, que requiere presupuesto confirmado primero)
- **Complejidad:** 🟡 Media

---

#### 2.5 Visualización y Gestión de Puntos en Clientes

**Qué hacer:** La tabla de clientes muestra la columna "Puntos" pero el dato no se muestra (no hay servicio que lo traiga junto con el cliente — la tabla `puntos` es separada de `clientes`).

**Funcionalidad a implementar:**
- Incluir `puntosAcumulados` en la respuesta de `GET /api/clientes` (join con tabla puntos)
- O crear `GET /api/clientes/{id}/puntos` (ítem 1.4)
- Mostrar correctamente en la tabla de clientes

- **Impacto:** BE+FE
- **Depende de:** 1.4 (Puntos endpoint)
- **Bloquea:** 2.3 (Cobro/Canje)
- **Complejidad:** 🟢 Baja

---

### Prioridad 3 — Cambios y mejoras sobre la app original

#### 3.1 Limpieza de Deuda Técnica en Backend

**Qué hacer:** Corregir bugs menores y código muerto detectados en el análisis.

| Item | Archivo | Problema | Acción |
|---|---|---|---|
| `LoginController` muerto | `LoginController.java` | Deshabilitado, nunca ejecuta | Eliminar archivo |
| Bug copy-paste | `MaterialesService.java:57` | `setIsMaterial(dto.getIsGrabado())` | Corregir a `setIsGrabado()` |
| Método mal nombrado | `PresupuestoService.java` | `updateCliente()` actualiza presupuesto | Renombrar a `updatePresupuesto()` |
| Typo en service | `PagosService.java` | `gepPagosByIDPresupuesto` | Renombrar a `getPagosByIDPresupuesto` |
| POST para update | `PresupuestoController.java` | `POST /presupuestos/update` | Cambiar a `PUT /presupuestos/{id}` |
| Código comentado | `PresupuestoService.java:90-152` | Mapeos de pagos/descuentos comentados | Limpiar o descomentar si necesario |
| No-op innecesario | `PresupuestoService.java:88` | `dto.setPrecioSinDescuento(dto.getPrecioSinDescuento())` | Eliminar |

- **Impacto:** BE
- **Depende de:** Nada
- **Complejidad:** 🟢 Baja

---

#### 3.2 Limpieza de Deuda Técnica en Frontend

**Qué hacer:** Corregir bugs menores y mejoras de UX detectadas.

| Item | Archivo | Problema | Acción |
|---|---|---|---|
| URL hardcodeada | `Calendarios.jsx` | `http://localhost:8090/...` hardcodeado | Mover a variable de entorno `VITE_LEGACY_URL` |
| Sin redirect post-registro | `Register.jsx` | No redirige a `/login` tras registro | Agregar `navigate('/login')` en success |
| Fecha hardcodeada | `Trabajos.jsx` | Columna "Fecha Entrega" muestra `-` | Conectar con dato real |

- **Impacto:** FE
- **Depende de:** Nada
- **Complejidad:** 🟢 Baja

---

#### 3.3 Endpoint y UI para `medio_pago` y `tarjeta`

**Qué hacer:** Las tablas `medio_pago` y `tarjeta` existen en la BD y en el modelo JPA, pero no hay endpoints para consultarlas. Son necesarias para el flujo de cobro.

| Endpoint | Descripción |
|---|---|
| `GET /api/utils/medio-pago` | Lista medios de pago disponibles |
| `GET /api/utils/tarjetas` | Lista tarjetas disponibles |
| `GET /api/utils/cuentas-bancarias` | Lista cuentas bancarias |

- **Impacto:** BE+FE
- **Depende de:** Nada
- **Bloquea:** 2.3 (Flujo de cobro)
- **Complejidad:** 🟢 Baja

---

#### 3.4 Gestión de Máquinas en la UI

**Qué hacer:** `MaquinasController` está completo en BE (GET, POST, PUT). Falta la pantalla de gestión en FE.

**Funcionalidad:**
- Pantalla de ABM de máquinas (habilitar/deshabilitar)
- Accesible desde Parámetros o menú propio

- **Impacto:** FE (BE listo)
- **Depende de:** Nada
- **Complejidad:** 🟢 Baja — mismo patrón que Materiales

---

#### 3.5 Gestión de Costos Fijos

**Qué hacer:** Las tablas `costo_fijo`, `costo_fijo_detalle`, `tipo_costo_fijo` existen en la BD y hay entidades JPA (`CostoFijo`, `CostoFijoDetalle`), pero no hay controller ni endpoints.

**Funcionalidad:**
- CRUD de costos fijos mensuales con detalle por tipo
- UI para gestionar el registro mensual de gastos fijos

- **Impacto:** BE+FE
- **Depende de:** Nada
- **Complejidad:** 🟡 Media

---

#### 3.6 Descuentos — Modelo completo

**Qué hacer:** La tabla `descuento` y `tipo_descuento` existen en la BD y en el modelo JPA (`Descuento`, `TipoDescuento`), pero no hay controller ni lógica activa.

**Funcionalidad:**
- Al cobrar un presupuesto, registrar el descuento aplicado en tabla `descuento`
- El tipo de descuento puede ser: PUNTOS, EFECTIVO, etc.
- Actualmente `PresupuestoService` tiene código comentado para esto (líneas 90-152)

- **Impacto:** BE+FE
- **Depende de:** 2.3 (Flujo de cobro)
- **Complejidad:** 🟡 Media

---

### Prioridad 4 — Secundario

#### 4.1 Generación de PDF de Presupuesto

**Qué hacer:** En el sistema original, se generaba un PDF al confirmar el presupuesto (usando iText). En el nuevo sistema no existe este endpoint.

**Funcionalidad:**
- `GET /api/presupuestos/{id}/pdf` → genera y descarga PDF con datos del cliente, trabajos, puntos, totales
- Botón "Descargar PDF" en la pantalla de Trabajos / Presupuestos

- **Impacto:** BE+FE
- **Depende de:** Nada (datos ya disponibles en BE)
- **Complejidad:** 🔴 Alta — requiere librería de PDF (iText 7 / OpenPDF), diseño del layout del documento

---

#### 4.2 Exportación de Cierre a Excel

**Qué hacer:** El sistema original exportaba el cierre a Excel (Apache POI). `ExportController` existe en el nuevo BE pero solo maneja archivos base64 genéricos.

**Funcionalidad:**
- `GET /api/cierres/{id}/excel` → genera y descarga Excel del cierre
- O generar en FE con librería como `xlsx`

- **Impacto:** BE+FE
- **Depende de:** 2.2 (Página Cierres)
- **Complejidad:** 🟡 Media

---

#### 4.3 Calendario de Máquinas (reemplazar iframe)

**Qué hacer:** Actualmente `Calendarios.jsx` es un `<iframe>` apuntando al sistema legacy en `localhost:8090`. Debe ser reemplazado por la implementación React real.

**Funcionalidad:**
- Migrar la vista del calendario a React nativo
- Consumir `GET /api/events` (endpoint ya existe y funciona)
- Eliminar dependencia del sistema Struts legacy

- **Impacto:** FE (BE listo)
- **Depende de:** Nada técnico (pero es más útil cuando el sistema legacy se dé de baja)
- **Complejidad:** 🔴 Alta — requiere componente de calendario (FullCalendar o similar), lógica de drag & drop para asignar máquinas

---

#### 4.4 Usuarios — Control de Acceso por Rol en Menú

**Qué hacer:** El botón "Usuarios" en el Layout está visible para todos los usuarios, pero la lógica de `isSuperAdmin` solo se aplica dentro de la página. El menú de navegación no filtra por rol.

**Funcionalidad:**
- Ocultar ítem "Usuarios" del menú para roles no admin
- Potencialmente restringir otros módulos según rol

- **Impacto:** FE
- **Depende de:** Nada
- **Complejidad:** 🟢 Baja

---

#### 4.5 Gestión de Parámetros — Sección de Máquinas Integrada

**Qué hacer:** La pantalla de Parámetros solo muestra `Varios`. Integrar la gestión de máquinas y tipos de cliente como pestañas o secciones dentro de la misma pantalla.

- **Impacto:** FE
- **Depende de:** 3.4 (Gestión de Máquinas)
- **Complejidad:** 🟢 Baja

---

#### 4.6 Mejoras de Performance en Queries

**Qué hacer:** Los índices ya fueron creados en el schema v2. Verificar que las queries JPA los usen correctamente.

| Check | Descripción |
|---|---|
| `clientes` búsqueda por nombre | Verificar que LIKE use `idx_nombre_cliente` |
| `presupuesto` por cliente | Verificar uso de `idx_presupuesto_id_cliente` |
| `ventas` por fecha | Verificar uso de `idx_ventas_fecha_venta` |
| N+1 en presupuestos | Verificar que no haya N+1 al cargar trabajos de presupuesto |

- **Impacto:** BE
- **Depende de:** Nada
- **Complejidad:** 🟢 Baja

---

### Resumen de la Hoja de Ruta

| # | Ítem | Impacto | Complejidad | Prioridad |
|---|---|---|---|---|
| 1.1 | Cierres CRUD endpoints | BE | 🟢 Baja | 1 |
| 1.2 | Pagos POST endpoints | BE | 🟡 Media | 1 |
| 1.3 | Compramateriales endpoints | BE | 🟢 Baja | 1 |
| 1.4 | Puntos endpoint | BE | 🟢 Baja | 1 |
| 1.5 | Completar presupuestosService.js | FE | 🟢 Baja | 1 |
| 1.6 | Completar utilsService.js | FE | 🟢 Baja | 1 |
| 2.1 | Página Ventas | FE | 🟡 Media | 2 |
| 2.2 | Página Cierres | BE+FE | 🔴 Alta | 2 |
| 2.3 | Flujo de Cobro con Medio de Pago | BE+FE | 🔴 Alta | 2 |
| 2.4 | Confirmación de Presupuesto (flujo completo) | FE+BE | 🟡 Media | 2 |
| 2.5 | Puntos en tabla de Clientes | BE+FE | 🟢 Baja | 2 |
| 3.1 | Limpieza deuda técnica BE | BE | 🟢 Baja | 3 |
| 3.2 | Limpieza deuda técnica FE | FE | 🟢 Baja | 3 |
| 3.3 | Endpoints medio_pago, tarjeta, cuentas | BE+FE | 🟢 Baja | 3 |
| 3.4 | Gestión de Máquinas UI | FE | 🟢 Baja | 3 |
| 3.5 | Gestión de Costos Fijos | BE+FE | 🟡 Media | 3 |
| 3.6 | Descuentos — modelo completo | BE+FE | 🟡 Media | 3 |
| 4.1 | Generación PDF | BE+FE | 🔴 Alta | 4 |
| 4.2 | Exportación Excel cierre | BE+FE | 🟡 Media | 4 |
| 4.3 | Calendario React (reemplazar iframe) | FE | 🔴 Alta | 4 |
| 4.4 | Control de acceso por rol en menú | FE | 🟢 Baja | 4 |
| 4.5 | Parámetros — sección máquinas integrada | FE | 🟢 Baja | 4 |
| 4.6 | Verificación de performance de queries | BE | 🟢 Baja | 4 |

---

## 7. Base de Datos

> **Original:** `precision_schema` (MySQL 8.0, charset `latin1` / `utf8mb3` mixto)
> **Nueva:** `precision_schema_v2` (MySQL 8.0, charset `utf8mb4` uniforme)
> **Migraciones:** Flyway (`flyway_schema_history`)

---

### 7.1 Convenciones de Nomenclatura

| Aspecto | Original | Nueva |
|---|---|---|
| Nombres de tablas | camelCase / minúsculas mixto | snake_case |
| Nombres de columnas | camelCase (`idCliente`, `nombreCliente`) | snake_case (`id_cliente`, `nombre_cliente`) |
| PKs | `idXxx` | `id_xxx` |
| Charset | `latin1` / `utf8mb3` mixto | `utf8mb4` uniforme |
| Tipos monetarios | `double` | `decimal(10,2)` / `decimal(12,2)` |
| Booleanos | `varchar(3)` con `'S'`/`'N'` o `'si'`/`'no'` | `tinyint(1)` |
| Fechas | `varchar(45)` / `varchar(255)` | `date`, `datetime`, `time` nativos |
| Foreign keys | Sin FKs declaradas | FKs explícitas con nombre (`fk_xxx`) |
| Índices | Sin índices de búsqueda | Índices en columnas de búsqueda frecuente |

---

### 7.2 Tablas Modificadas

#### `cierre`
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idcierre` | `id_cierre` | Renombrado |
| `montoInicial` | `monto_inicial` | Renombrado + `double` → `decimal(12,2) NOT NULL DEFAULT 0` |
| `montoFinal` | `monto_final` | Renombrado + tipo |
| `montoExtracciones` | `monto_extracciones` | Renombrado + tipo |
| `montoPresupuestos` | `monto_presupuestos` | Renombrado + tipo |
| `arqueo` | `arqueo` | Tipo `double` → `decimal(12,2)` |
| `diferencia` | `diferencia` | Tipo `double` → `decimal(12,2)` |
| `responsable` varchar(45) | `responsable` varchar(100) | Ampliado |
| `fechaCierre` date | `fecha_cierre` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP | Tipo extendido + default |
| `senia` | `senia` | Tipo → `decimal(12,2) NOT NULL DEFAULT 0` |
| `ventas` | `ventas` | Tipo → `decimal(12,2) NOT NULL DEFAULT 0` |
| `montoCompraMateriales` | `monto_compra_materiales` | Renombrado + tipo |
| `mesCierre` | `mes_cierre` | Renombrado |
| *(no existía)* | `descuento_efectivo decimal(12,2)` | **NUEVO** — descuento en efectivo aplicado |
| *(no existía)* | `id_user int NOT NULL` | **NUEVO** — FK al usuario que cerró la caja |

#### `clientes`
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idCliente` | `id_cliente` | Renombrado |
| `dniCliente varchar(255)` | `dni_cliente varchar(50)` | Renombrado + reducido |
| `emailCliente varchar(255)` | `email_cliente varchar(100)` | Renombrado + reducido |
| `nombreCliente varchar(255)` | `nombre_cliente varchar(200)` | Renombrado + reducido |
| `telefonoCliente varchar(255)` | `telefono_cliente varchar(100)` | Renombrado + reducido |
| `mora varchar(45)` | `mora tinyint(1)` | Renombrado + `varchar` → boolean |
| `tipoCliente varchar(45)` | `id_tipo_cliente int` → FK a `tipo_cliente` | Desnormalizado → normalizado |
| `precioMinutoEmpresa double` | `precio_minuto_empresa decimal(10,2)` | Renombrado + tipo |
| `fechaCreacion varchar(45)` | `fecha_creacion datetime DEFAULT CURRENT_TIMESTAMP` | Tipo correcto + default automático |
| *(no existía)* | `disabled tinyint(1) DEFAULT 0` | **NUEVO** — soft delete |
| *(sin índices)* | Índices en nombre, mora, DNI, tipo, fecha | **NUEVOS** índices de búsqueda |

#### `control_puntos` (era `controlpuntos`)
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idControlPuntos` | `id_control_puntos` | Renombrado |
| `idPresupuesto` | `id_presupuesto` | Renombrado + FK declarada |
| `puntosAcumulados` | `puntos_acumulados` | Renombrado + DEFAULT 0 |
| `puntosAcumuladosHistoricos` | `puntos_acumulados_historicos` | Renombrado + DEFAULT 0 |
| `puntosPorCorteTrabajos` | `puntos_por_corte_trabajos` | Renombrado + DEFAULT 0 |
| `puntosTotalesTrabajo` | `puntos_totales_trabajo` | Renombrado + DEFAULT 0 |
| `minutosDisponibles` | `minutos_disponibles` | Renombrado + DEFAULT 0 |
| `minutosCanjeados` | `minutos_canjeados` | Renombrado + DEFAULT 0 |
| `puntosCanjeados` | `puntos_canjeados` | Renombrado + DEFAULT 0 |
| `puntosAcumuladosNuevos` | `puntos_acumulados_nuevos` | Renombrado + DEFAULT 0 |
| `puntosAcumuladosHistoricosNuevo` | `puntos_acumulados_historicos_nuevo` | Renombrado + DEFAULT 0 |
| `precioMinuti double` | `precio_minuto decimal(10,2)` | Renombrado + tipo corregido |
| `fechaCanjePuntos varchar(45)` + `horaCanjePuntos varchar(45)` | `fecha_canje_puntos datetime` | Dos campos varchar → un datetime |

#### `extracciones`
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idextraccion` | `id_extraccion` | Renombrado |
| `fechaExtraccion varchar(45)` + `mesExtraccion varchar(45)` | `fecha_extraccion datetime NOT NULL` | Dos campos varchar → un datetime |
| `montoExtraccion double` | `monto_extraccion decimal(12,2)` | Renombrado + tipo |
| `motivoExtraccion varchar(45)` | `motivo_extraccion varchar(255)` | Renombrado + ampliado |
| `responsableExtraccion varchar(45)` | `responsable_extraccion varchar(100)` | Renombrado + ampliado |
| *(no existía)* | `id_usuario int` | **NUEVO** — FK al usuario que realizó la extracción |
| `mesExtraccion` | *(eliminado)* | Absorbido por `fecha_extraccion` |

#### `materiales`
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idmateriales` | `id_materiales` | Renombrado |
| `materiales varchar(45)` | `materiales varchar(45)` | Sin cambio |
| *(no existía)* | `is_material tinyint(1) DEFAULT 0` | **NUEVO** — flag: es un material de corte |
| *(no existía)* | `is_grabado tinyint(1) DEFAULT 0` | **NUEVO** — flag: es un trabajo de grabado |
| *(no existía)* | `disabled tinyint(1) NOT NULL DEFAULT 0` | **NUEVO** — soft delete |

#### `precio_materiales` (era `preciomateriales`)
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idprecioMateriales` | `id_precio_materiales` | Renombrado |
| `idMateriales` | `id_materiales` | Renombrado |
| `superficie varchar(45)` | `superficie varchar(45)` + `id_superficie tinyint unsigned` | Agregada FK a tabla `superficies` |
| `precioMaterial double` | `precio_material decimal(10,2) NOT NULL` | Tipo + NOT NULL |
| *(no existía)* | `unidades tinyint unsigned` | **NUEVO** |

#### `presupuesto`
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idPresupuesto` | `id_presupuesto` | Renombrado |
| `fechaPresupuesto varchar` + `horaPresupuesto varchar` | `fecha_hora_presupuesto datetime NOT NULL` | Dos varchars → un datetime |
| `idCliente` | `id_cliente` | Renombrado + FK declarada |
| `precioCobrado double NOT NULL` | `precio_cobrado decimal(10,2) NOT NULL` | Tipo |
| `precioSinCanje double NOT NULL` | `precio_sin_descuento decimal(10,2) NOT NULL` | **Renombrado** — refleja que aplica a todos los tipos de descuento |
| `puntosDisponibleCanje int NOT NULL` | *(eliminado)* | Calculado dinámicamente |
| `puntosDisponibles int NOT NULL` | *(eliminado)* | Calculado dinámicamente |
| `puntosCanjeados int NOT NULL` | `puntos_canjeados int NOT NULL` | Renombrado |
| `aprobado varchar(3)` | `aprobado tinyint NOT NULL` | `varchar` → boolean |
| `realizado varchar(3)` | `realizado tinyint NOT NULL` | `varchar` → boolean |
| `cobrado varchar(3)` | `cobrado tinyint NOT NULL` | `varchar` → boolean |
| `entregado varchar(3)` | `entregado tinyint NOT NULL` | `varchar` → boolean |
| `fechaCobrado varchar(45)` | `fecha_cobrado datetime` | Tipo correcto |
| `fechaRealizado varchar(45)` | `fecha_realizado datetime` | Tipo correcto |
| `precioMinuto double` | `precio_minuto decimal(10,2)` | Tipo |
| `senia double` + `fechaSenia varchar` | *(eliminados de presupuesto)* | Movido a `pago_presupuesto` con tipo SENIA |
| `maquina varchar(4)` | *(eliminado de presupuesto)* | Movido a `events` → FK a `maquinas` |
| `puntosAcumuladosPdf int` | *(eliminado)* | Revisar si necesario |
| *(sin índices)* | Índices en `id_cliente` y `fecha_hora_presupuesto` | **NUEVOS** |

#### `puntos`
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idPuntos` | `id_puntos` | Renombrado |
| `fechaPrimerPunto varchar(255)` | `fecha_primer_punto date` | Tipo correcto |
| `idCliente int NOT NULL` | `id_cliente int` | Renombrado + FK declarada |
| `puntosAcumulados int NOT NULL` | `puntos_acumulados int NOT NULL` | Renombrado |
| `puntosAcumuladosHistorico int NOT NULL` | `puntos_acumulados_historico int NOT NULL` | Renombrado |

#### `trabajo_presupuestado` (era `trabajopresupuestado`)
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idTrabajoPresupuestado` | `id_trabajo_presupuestado` | Renombrado |
| `seleccionado tinyint(1)` | `seleccionado tinyint(1) DEFAULT 0` | Agregado default |
| `archivoCad varchar(255)` | `archivo_cad varchar(255)` | Renombrado |
| `archivoOriginal varchar(255)` | `archivo_original varchar(255)` | Renombrado |
| `fechaRealizacion varchar(255)` | *(eliminado)* | No necesario en nuevo modelo |
| `horaRealizacion varchar(255)` | *(eliminado)* | No necesario en nuevo modelo |
| `idPResupuesto int NOT NULL` | `id_presupuesto int NOT NULL` | Renombrado + FK declarada |
| `material varchar(255)` | `material varchar(255)` | Sin cambio (nombre descriptivo para display) |
| `notas varchar(255)` | `notas varchar(255)` | Sin cambio |
| `precioMaterial double NOT NULL` | `precio_material decimal(10,2) NOT NULL` | Renombrado + tipo |
| `precioTrabajo double NOT NULL` | `precio_trabajo decimal(10,2) NOT NULL` | Renombrado + tipo |
| `puntosPorCorte int NOT NULL` | *(eliminado)* | Lógica de puntos separada |
| `tiempoDeCorte int NOT NULL` | `tiempo_de_corte int NOT NULL` | Renombrado |
| `precioCorte double` | `precio_corte decimal(10,2) DEFAULT 0` | Renombrado + tipo + default |
| `vinilo double` | `vinilo decimal(10,2) DEFAULT 0` | Tipo + default |
| `vectorizado double` | `vectorizado decimal(10,2) DEFAULT 0` | Tipo + default |
| `disenio double` | `extra decimal(10,2) DEFAULT 0` | **Renombrado** — generalizado para cualquier extra |
| `precioMinuto double` | `precio_minuto decimal(10,2) DEFAULT 0` | Renombrado + tipo + default |
| `fechaRealizado varchar(45)` | *(eliminado)* | No necesario |
| *(no existía)* | `descuento decimal(10,2)` | **NUEVO** — descuento por trabajo |
| *(no existía)* | `id_materiales int` + FK | **NUEVO** — FK a tabla materiales |
| *(no existía)* | `id_superficie int unsigned` + FK | **NUEVO** — FK a tabla superficies |
| *(sin índices)* | Índices en presupuesto, seleccionado, material, superficie | **NUEVOS** |

#### `varios`
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idVarios` | `id_varios` | Renombrado |
| `precioMinuto double` | `precio_minuto decimal(10,2)` | Renombrado + tipo |
| `puntosPorMinuto int` | *(eliminado)* | Lógica simplificada |
| `horaInicio time` | `hora_inicio time` | Renombrado |
| `horaCierre time` | `hora_cierre time` | Renombrado |
| `ajuste double` | `ajuste decimal(10,2)` | Tipo |
| `horaInicioFdS varchar(45)` | `hora_inicio_fds time` | Tipo correcto |
| `horaCierreFdS varchar(45)` | `hora_cierre_fds time` | Tipo correcto |
| *(no existía)* | `precio_minuto_empresa decimal(10,2)` | **NUEVO** — precio global para tipo EMPRESA |
| *(no existía)* | `descuento_efectivo decimal(10,2)` | **NUEVO** — descuento global en efectivo |

#### `ventas`
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idventas` | `id_ventas` | Renombrado |
| `fechaventa varchar(45)` | `fecha_venta date` | Tipo correcto |
| `horaventa varchar(45)` | `hora_venta time` | Tipo correcto |
| `material varchar(45)` | `material varchar(45)` | Sin cambio |
| `superficie varchar(45)` | `superficie varchar(45)` | Sin cambio |
| `preciomaterial double` | `precio_material decimal(10,2)` | Renombrado + tipo |
| `cantidad int` | `cantidad int NOT NULL` | NOT NULL |
| `precioventa double` | `precio_venta decimal(10,2) NOT NULL` | Renombrado + tipo + NOT NULL |
| *(no existía)* | `id_materiales int NOT NULL` + FK | **NUEVO** — FK a materiales |
| *(sin índices)* | Índices en materiales y fecha | **NUEVOS** |

#### `events`
| Campo original | Campo nuevo | Cambio |
|---|---|---|
| `idPresupuesto` | `id_presupuesto` | Renombrado |
| `idTrabajo` | `id_trabajo` | Renombrado |
| *(no existía)* | `id_maquina int NOT NULL` + FK a `maquinas` | **NUEVO** — FK a tabla maquinas |

---

### 7.3 Tablas Nuevas

| Tabla | Descripción |
|---|---|
| `users` | Usuarios del sistema con autenticación (username, password hash, role) |
| `tipo_cliente` | Catálogo normalizado: ESTUDIANTE, NORMAL, EMPRESA |
| `tipo_descuento` | Catálogo de tipos de descuento (PUNTOS, EFECTIVO, etc.) |
| `descuento` | Descuento aplicado a un presupuesto: 1:1 con presupuesto, referencia tipo |
| `pago_presupuesto` | Registro detallado de cada pago/seña de un presupuesto: medio, tipo, monto, tarjeta, cuotas, cuenta bancaria |
| `pago_venta` | Ídem para ventas directas |
| `medio_pago` | Catálogo: EFECTIVO, TARJETA_DEBITO, TARJETA_CREDITO, TRANSFERENCIA, etc. |
| `tipo_pago` | Catálogo: SENIA, PAGO, DEVOLUCION, etc. |
| `tarjeta` | Catálogo de tarjetas (Visa, Mastercard, etc.) |
| `cuenta_bancaria` | Cuentas bancarias del negocio (banco, CBU, alias, moneda) |
| `maquinas` | Catálogo de máquinas del taller (nombre, habilitada, fecha creación) |
| `superficies` | Catálogo de fracciones: `1`, `3/4`, `1/2`, `1/4` |
| `flyway_schema_history` | Historial de migraciones Flyway |
| `varios_historial` | Auditoría de cambios a la tabla `varios` (quién cambió qué y cuándo) |
| `costo_fijo` | Cabecera de costos fijos mensuales (periodo, total, fecha_cambio) |
| `costo_fijo_detalle` | Detalle por tipo de costo (FK a `tipo_costo_fijo`) |
| `tipo_costo_fijo` | Catálogo de tipos de costo fijo (electricidad, afip, alquiler, etc.) |

---

### 7.4 Tablas Sin Cambios Estructurales

Las siguientes tablas se mantienen igual (solo renombrando convención de nombres):

`cajaahorro`, `cajamensual`, `cierrecuatrimestre`, `compramateriales`, `costosvariables`, `departamentos`, `montoinicialcaja`, `movimientocajaahorro`

> **Nota:** `costosfijos` se mantiene en v2 como tabla legacy pero existe el nuevo modelo normalizado `costo_fijo` / `costo_fijo_detalle` / `tipo_costo_fijo` que la reemplaza.

---

### 7.5 Resumen de Cambios de Schema

| Categoría | Cantidad |
|---|---|
| Tablas con cambios de estructura | 10 |
| Tablas renombradas | 3 (`trabajopresupuestado`, `controlpuntos`, `preciomateriales`) |
| Tablas nuevas | 17 |
| Tablas sin cambios relevantes | 8 |
| Campos eliminados | ~15 (calculables, varchars de fecha, campos desnormalizados) |
| Campos nuevos | ~20 (FKs, flags, tipos correctos) |

---

### 7.6 Migración de Fechas a UTC

> **Problema:** Todos los datos históricos de la base original están en horario de Argentina (ART, UTC-3).
> Argentina **no aplica horario de verano desde 2008**, por lo que el offset es constante: `UTC = ART + 3 horas`.
>
> **Objetivo:** Almacenar todas las fechas/horas absolutas en UTC para que la aplicación sea independiente
> del timezone del servidor donde se despliega.

---

#### 7.6.1 Origen de los datos históricos

| Campo original | Formato | Zona horaria |
|---|---|---|
| `fechaPresupuesto` + `horaPresupuesto` varchar | `dd/MM/yyyy` + `HH:mm:ss` | ART (UTC-3) |
| `fechaCobrado` varchar | `dd/MM/yyyy HH:mm:ss` | ART (UTC-3) |
| `fechaRealizado` varchar | `dd/MM/yyyy HH:mm:ss` | ART (UTC-3) |
| `fechaCreacion` varchar (clientes) | `dd/MM/yyyy HH:mm:ss` | ART (UTC-3) |
| `fechaExtraccion` varchar | `dd/MM/yyyy` | ART (sin componente horaria) |
| `fechaCanjePuntos` + `horaCanjePuntos` varchar | `dd/MM/yyyy` + `HH:mm:ss` | ART (UTC-3) |
| `fechaventa` + `horaventa` varchar (ventas) | `dd/MM/yyyy` + `HH:mm:ss` | ART (UTC-3) |

Los scripts Flyway V3–V37 convierten estos varchars a tipos nativos (`DATE`, `TIME`, `DATETIME`) pero
**sin aplicar conversión de zona horaria**. Los valores quedaron almacenados como ART en columnas DATETIME.

---

#### 7.6.2 Script pendiente: `V49__convert_datetimes_to_utc.sql`

Convierte los campos DATETIME aplicando `+ INTERVAL 3 HOUR` (ART → UTC).

**Campos que SÍ requieren conversión (tienen componente horaria ART):**

| Tabla | Campo | Tipo | Script que lo creó |
|---|---|---|---|
| `clientes` | `fecha_creacion` | `DATETIME` | V3 |
| `presupuesto` | `fecha_hora_presupuesto` | `DATETIME` | V4 |
| `presupuesto` | `fecha_cobrado` | `DATETIME` | V4 |
| `presupuesto` | `fecha_realizado` | `DATETIME` | V4 |
| `pago_presupuesto` | `fecha_hora` | `DATETIME` | V8 — copiado de `presupuesto.fecha_cobrado` |
| `control_puntos` | `fecha_canje_puntos` | `DATETIME` | V27 |

**Campos que NO requieren conversión:**

| Tabla | Campo | Motivo |
|---|---|---|
| `ventas` | `fecha_venta` | `DATE` puro: sin componente horaria — ver sección 7.6.3 |
| `ventas` | `hora_venta` | `TIME` operativo: no representa instante absoluto |
| `cierre` | `fecha_cierre` | Derivado de `DATE` original (hora = 00:00:00); la fecha de negocio no cambia con +3h |
| `extracciones` | `fecha_extraccion` | Derivado de VARCHAR solo-fecha (sin hora); ídem cierre |
| `puntos` | `fecha_primer_punto` | `DATE` puro |
| `varios` | `hora_inicio`, `hora_cierre`, `hora_inicio_fds`, `hora_cierre_fds` | Horarios operativos, son relativos al timezone local del negocio — no son instantes absolutos |

> **Nota:** `varios_historial.fecha_cambio` usa `DEFAULT CURRENT_TIMESTAMP`. Su valor correcto depende
> de la configuración de timezone del servidor MySQL y de la JVM en el momento del INSERT, no de este
> script de migración. Se resuelve con la configuración de aplicación (sección 7.6.4).

---

#### 7.6.3 Caso especial: tabla `ventas` (DATE + TIME separados)

La tabla `ventas` almacena `fecha_venta DATE` y `hora_venta TIME` como columnas separadas.
Esto genera un problema al convertir a UTC: una venta registrada a las **22:30 ART** del día `2025-01-15`
corresponde en UTC a **01:30 del día `2025-01-16`** — la fecha cambia.

**Script pendiente: `V50__ventas_unify_datetime_utc.sql`**

Pasos a implementar:
1. Agregar columna `fecha_hora_venta DATETIME NULL`
2. Poblarla combinando `fecha_venta` + `hora_venta` y convirtiendo a UTC:
   ```sql
   UPDATE ventas
   SET fecha_hora_venta = DATE_ADD(
       STR_TO_DATE(CONCAT(fecha_venta, ' ', hora_venta), '%Y-%m-%d %H:%i:%s'),
       INTERVAL 3 HOUR
   )
   WHERE fecha_venta IS NOT NULL AND hora_venta IS NOT NULL;
   ```
3. Mantener `fecha_venta DATE` y `hora_venta TIME` como columnas de compatibilidad (o eliminarlas
   en un script posterior una vez actualizado el código de aplicación).

> ⚠️ Este script requiere coordinación con el BE: `VentaService` y `Venta.java` deben usar
> `fecha_hora_venta` en lugar de los dos campos separados.

---

#### 7.6.4 Configuración de aplicación requerida (complementaria al script)

El script V49 convierte los datos históricos. Para que los **nuevos registros** también se almacenen en UTC,
se deben aplicar las siguientes configuraciones:

**`application.properties`** (ya verificar/agregar):
```properties
# Fuerza Hibernate a escribir y leer DATETIME en UTC
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

**JDBC URL** (en `spring.datasource.url`):
```
?serverTimezone=UTC&useLegacyDatetimeCode=false
```

**JVM** (en `JAVA_TOOL_OPTIONS` o script de arranque):
```
-Duser.timezone=UTC
```

**MySQL server** (opcional pero recomendado para consistencia):
```sql
SET GLOBAL time_zone = '+00:00';
```
O en `my.cnf`:
```
[mysqld]
default-time-zone = '+00:00'
```

> Sin estas configuraciones, aunque los datos históricos queden en UTC tras V49, los nuevos INSERTs
> con `DEFAULT CURRENT_TIMESTAMP` o `NOW()` seguirán usando el timezone del servidor.

---

#### 7.6.5 Regla para nuevos campos de fecha en el schema

A partir de V49, **todo campo que represente un instante absoluto debe ser `DATETIME` y almacenarse en UTC**.
El frontend es responsable de convertir UTC → timezone local del usuario para mostrar.

| Tipo de dato | Convención |
|---|---|
| Instante absoluto (cuándo ocurrió algo) | `DATETIME` en UTC — ej: `fecha_hora_venta`, `fecha_cobrado` |
| Fecha de negocio (día calendario, sin hora) | `DATE` — ej: `fecha_cierre`, `fecha_primer_punto` |
| Hora operativa (apertura, cierre del local) | `TIME` — relativa al timezone del negocio, no UTC |

---

#### 7.6.6 `DATETIME` vs `TIMESTAMP` — cuándo usar cada uno

MySQL tiene dos tipos para almacenar fechas con hora. La diferencia es crítica para el manejo de UTC:

| | `DATETIME` | `TIMESTAMP` |
|---|---|---|
| Almacenamiento | Valor literal, sin conversión | Siempre UTC internamente |
| `DEFAULT CURRENT_TIMESTAMP` | Usa timezone de la **sesión** — frágil | Siempre UTC — robusto |
| Al leer | Devuelve lo almacenado literal | Convierte UTC → timezone de sesión |
| Rango | `1000-01-01` a `9999-12-31` | `1970-01-01` a `2038-01-19` |
| Caso de uso | Instantes enviados explícitamente por la app (Hibernate los envía ya en UTC) | Instantes auto-generados por MySQL (`DEFAULT CURRENT_TIMESTAMP`, triggers) |

**Script pendiente: `V50__convert_fecha_hora_to_timestamp.sql`**

Convierte a `TIMESTAMP` los campos que usan `DEFAULT CURRENT_TIMESTAMP` y representan instantes absolutos:

| Tabla | Campo | Motivo |
|---|---|---|
| `pago_presupuesto` | `fecha_hora` | Instante del pago, auto-generado por MySQL |
| `pago_venta` | `fecha_hora` | Idem para ventas |
| `varios_historial` | `fecha_cambio` | Auditoría de cambios de configuración |

> ⚠️ **Orden de ejecución:** V50 debe correr **después de V49** (que convirtió los datos a UTC)
> y **con la sesión MySQL en UTC**. Al ejecutar el `ALTER TABLE`, MySQL re-interpreta los valores
> DATETIME existentes usando el timezone de sesión para almacenarlos como TIMESTAMP. Si la sesión
> fuera ART, desplazaría los valores ya-UTC otras 3 horas incorrectamente.
> El script incluye `SET time_zone = '+00:00'` como salvaguarda.

**Campo que NO se convierte a `TIMESTAMP`:**

| Tabla | Campo | Motivo |
|---|---|---|
| `cierre.fecha_cierre` | `DATETIME` | Es una fecha de negocio (día de cierre), no un instante de sistema. Conceptualmente es un `DATE`; se mantiene `DATETIME` por compatibilidad con el modelo actual. |

---

### 7.7 Inconsistencias en Naming de Scripts Flyway

> **Problema:** Flyway requiere el formato `V{version}__{descripción}.sql` (doble guión bajo).
> Varios scripts existentes usan **un solo guión bajo**, lo que puede causar que Flyway los ignore
> o falle al ordenarlos.

**Scripts con naming incorrecto (un solo `_`):**

| Script actual | Correcto sería |
|---|---|
| `V15_migracion_ventas.sql` | `V15__migracion_ventas.sql` |
| `V17_otras_migraciones.sql` | `V17__otras_migraciones.sql` |
| `V25_actualizacion_manejo_precios.sql` | `V25__actualizacion_manejo_precios.sql` |
| `V26_update_trabajo_con_superficies.sql` | `V26__update_trabajo_con_superficies.sql` |
| `V27_core_fk_cierre_user.sql` | `V27__core_fk_cierre_user.sql` |
| `V28_core_fk_trabajo_presupuestado.sql` | `V28__core_fk_trabajo_presupuestado.sql` |
| `V29_core_fk_events_presupuesto_trabajo.sql` | `V29__core_fk_events_presupuesto_trabajo.sql` |
| `V31_crear_tabla_venta.sql` | `V31__crear_tabla_venta.sql` |
| `V32_fk_pago_venta.sql` | `V32__fk_pago_venta.sql` |
| `V33_migrar_datos_ventas.sql` | `V33__migrar_datos_ventas.sql` |

> **Acción:** Renombrar estos archivos y registrar el cambio en `flyway_schema_history` si la base
> ya fue migrada con los nombres incorrectos (Flyway valida el checksum del nombre + contenido).
> Si la migración se corre desde cero en un ambiente nuevo, simplemente renombrarlos es suficiente.

> **Nota adicional:** Existen conflictos de número de versión entre los scripts "legacy" (V25–V29 con
> un `_`) y los nuevos (V25–V28 con doble `__`). Esto debe resolverse antes de correr Flyway en producción.

---

### 7.8 Scripts Flyway Pendientes — Resumen

> Scripts a crear o ya creados para completar la migración de DB.
> Ejecutar en orden estricto. V49 y V50 requieren sesión MySQL en UTC.

| Script | Descripción | Estado |
|---|---|---|
| `V49__convert_datetimes_to_utc.sql` | Convierte campos DATETIME históricos de ART a UTC (+3h) | ✅ Creado |
| `V50__convert_fecha_hora_to_timestamp.sql` | Cambia a `TIMESTAMP` los campos con `DEFAULT CURRENT_TIMESTAMP` | ✅ Creado |
| `V51__fk_faltantes_users_y_fix_pago_venta.sql` | FK `cierre.id_user → users`, FK `extracciones.id_usuario → users`, corrección IDs hardcodeados en `pago_venta` (V46) | ✅ Creado |
| `V52__migracion_compramateriales.sql` | Migración completa de `compramateriales`: snake_case, DECIMAL, DATETIME UTC, FK user, índices | ✅ Creado |
| `V53__ventas_unify_datetime_utc.sql` | Unifica `fecha_venta DATE` + `hora_venta TIME` en `fecha_hora_venta DATETIME` UTC (ver sección 7.6.3) | ❌ Pendiente |
| `V54__charset_database.sql` | `ALTER DATABASE precision_schema_v2 CHARACTER SET utf8mb4` | ❌ Pendiente |

**FKs que YA existen (verificadas):**
- `pago_venta → ventas`: creada en V44
- `trabajo_presupuestado → materiales`: creada en V28
- `trabajo_presupuestado → superficies`: creada en V28

**Problema conocido en V46** (corregido por V51):
V46 inserta registros en `pago_venta` usando `id_tipo_pago = 5` e `id_medio_pago = 7` hardcodeados.
Si los catálogos `tipo_pago` y `medio_pago` se insertaron en orden diferente, los IDs no coinciden.
V51 normaliza estos valores usando el nombre del catálogo como referencia.

---

## 8. Cambios Funcionales Planificados

### 8.1 Funcionalidades que Cambian

#### Autenticación y Sesión
| Original | Nuevo | Motivo |
|---|---|---|
| Sin autenticación real (UserLoginForm sin mapeo activo) | JWT con tabla `users` y roles | Seguridad básica inexistente en el sistema legacy |
| Sin control de quién realiza cada operación | `id_user` en cierres, extracciones, historial de parámetros | Trazabilidad y auditoría |

#### Gestión de Cobros y Medios de Pago
| Original | Nuevo | Motivo |
|---|---|---|
| Cobro = flag `cobrado = 'S'` en presupuesto | Tabla `pago_presupuesto` con detalle de medio de pago | Registro detallado para conciliación |
| Seña = campo `senia double` en presupuesto | Seña = registro en `pago_presupuesto` con `tipo_pago = SENIA` | Unifica el modelo de pagos |
| Solo efectivo implícito | Efectivo, tarjeta débito, tarjeta crédito (con cuotas), transferencia, cuenta bancaria | Refleja la realidad operativa |
| Sin registro de fecha/hora exacta del cobro | `fecha_hora datetime` en `pago_presupuesto` | Trazabilidad |

#### Descuentos
| Original | Nuevo | Motivo |
|---|---|---|
| Solo descuento por canje de puntos (lógica inline) | Tabla `descuento` + `tipo_descuento`: PUNTOS, EFECTIVO, otros | Extensibilidad y claridad |
| `precioSinCanje` en presupuesto | `precio_sin_descuento` en presupuesto | Renombrado para reflejar todos los tipos de descuento |
| Descuento calculado solo en frontend/backend al cobrar | `descuento_efectivo` configurable en `varios` + `descuento` por presupuesto | Descuento en efectivo como política del negocio |

#### Máquinas
| Original | Nuevo | Motivo |
|---|---|---|
| `maquina varchar(4)` en presupuesto y events | Tabla `maquinas` normalizada + FK en `events` | Permite gestionar máquinas: agregar, deshabilitar |
| Número de máquina sin nombre | `nombre_maquina` en tabla | Legibilidad |

#### Tipos de Cliente
| Original | Nuevo | Motivo |
|---|---|---|
| `tipoCliente varchar(45)` con valores hardcodeados | Tabla `tipo_cliente` + `id_tipo_cliente` FK | Extensible sin cambiar código |
| ESTUDIANTE / NORMAL / EMPRESA en código | Catálogo en base de datos | Configurable en runtime |

#### Materiales
| Original | Nuevo | Motivo |
|---|---|---|
| Sin distinción entre tipos de trabajo | Flags `is_material` e `is_grabado` en `materiales` | Permite filtrar por tipo de trabajo en la UI |
| Sin soft delete | `disabled tinyint(1)` en materiales y clientes | Preservar historial sin borrar registros |

#### Parámetros del Sistema (tabla `varios`)
| Original | Nuevo | Motivo |
|---|---|---|
| Sin historial de cambios | `varios_historial` registra cada modificación | Auditoría de cambios de precio/minuto |
| `puntosPorMinuto` en parámetros | Eliminado de `varios` | Simplificación del modelo de puntos |
| `horaInicioFdS varchar` / `horaCierreFdS varchar` | `hora_inicio_fds time` / `hora_cierre_fds time` | Tipo correcto |
| Sin precio/minuto para empresa a nivel global | `precio_minuto_empresa` en `varios` | Precio base para clientes EMPRESA |

#### Superficies de Material
| Original | Nuevo | Motivo |
|---|---|---|
| Fracciones como string: "1/4", "1/2", "3/4", "1" hardcodeadas | Tabla `superficies` + `id_superficie` FK | Extensibilidad, integridad referencial |

#### Costos Fijos
| Original | Nuevo | Motivo |
|---|---|---|
| Tabla `costosfijos` con columnas fijas por tipo (electricidad, afip, alquiler, etc.) | `costo_fijo` + `costo_fijo_detalle` + `tipo_costo_fijo` | Normalizado: permite agregar/quitar tipos sin cambiar el schema |
| Campos hardcodeados en código Java y JSP | Catálogo dinámico `tipo_costo_fijo` | Configurable |

#### Presupuesto — Campos Calculados
| Original | Nuevo | Motivo |
|---|---|---|
| `puntosDisponibles` y `puntosDisponibleCanje` guardados en presupuesto | Calculados dinámicamente por el backend | Evitar datos stale; single source of truth = tabla `puntos` |
| `puntosAcumuladosPdf` guardado en presupuesto | Calculado al generar PDF | Simplificación |

#### Trabajo Presupuestado — Campos Eliminados
| Campo eliminado | Motivo |
|---|---|
| `puntosPorCorte` | La lógica de puntos se desacoplió del trabajo individual |
| `fechaRealizacion` / `horaRealizacion` | Las fechas de realización se derivan de `events` (scheduler) |
| `disenio` → renombrado a `extra` | Generalizado para cualquier costo extra |

---

### 8.2 Funcionalidades Nuevas (no existían en Struts)

| Funcionalidad | Descripción |
|---|---|
| **Autenticación JWT** | Login con username/password, token JWT con expiración, roles de usuario |
| **Multi-medio de pago** | Registro de pagos con tarjeta (débito/crédito), transferencia, cuenta bancaria, cuotas |
| **Descuento en efectivo** | Además del canje de puntos, se puede aplicar un descuento directo en $ |
| **Gestión de máquinas** | CRUD de máquinas del taller (habilitar/deshabilitar) |
| **Historial de parámetros** | Auditoría de todos los cambios a precio/minuto y configuración del sistema |
| **Soft delete de clientes y materiales** | Campo `disabled` en lugar de borrado físico |
| **Costos fijos normalizados** | CRUD de tipos de costo y registro mensual con detalle por categoría |
| **Tabla de superficies** | Catálogo editable de fracciones de material |
| **Flyway migrations** | Migraciones de base de datos versionadas y auditadas |
| **API REST con Swagger** | Documentación automática de endpoints (SpringDoc/Swagger) |
| **CORS configurado** | Separación real frontend/backend con política de CORS |
| **Exportaciones** | `ExportController` dedicado para exportaciones (Excel, PDF) |

---

### 8.3 Flujos de Pantallas que Cambian

#### Cobro de Presupuesto
```
ANTES (Struts):
  Listado presupuestos → [Canjear puntos] → marcaPresupuestoCobrado
  → flag cobrado=S + descuenta puntos

NUEVO (React + Spring Boot):
  Listado presupuestos → [Cobrar]
  → Panel de cobro con:
     - Tipo de descuento (puntos / efectivo / ninguno)
     - Medio de pago (efectivo / tarjeta / transferencia)
     - Si tarjeta: tipo (débito/crédito), nombre tarjeta, cuotas
     - Si transferencia: cuenta bancaria destino
  → POST /pagos/presupuesto
  → Presupuesto marcado cobrado
```

#### Alta de Cliente
```
ANTES: Form simple → INSERT clientes + INSERT puntos
NUEVO: Form con tipo cliente (ahora FK) + precio especial empresa
       → La lógica de filesystem (crear carpetas) DESAPARECE
       → No se crean carpetas locales
```

#### Confirmación de Presupuesto (Trabajos)
```
ANTES: calcularPresupuesto → marcarPresupuestoRealizadoNoCLiente → genera PDF local
NUEVO: Calcular totales en tiempo real desde el frontend
       → Confirmar → POST /presupuestos/{id}/realizar
       → PDF se genera on-demand vía endpoint, no automáticamente
```

#### Cierre de Caja
```
ANTES: Vista de frameset, recarga por reload pages
NUEVO: SPA React con actualización reactiva
       → Cierres con identificación de usuario (id_user)
       → Extracciones con fecha datetime en lugar de varchar
```

#### Navegación General
```
ANTES: Frameset HTML con menu.jsp + frame de contenido
       → Recargas de página completa (JSP forward)
NUEVO: SPA con React Router
       → Navegación sin recarga
       → Estado gestionado en frontend
```

---

### 8.4 Mejoras de Performance Planificadas

| Área | Mejora | Detalle |
|---|---|---|
| **Índices de BD** | Búsqueda de clientes por nombre | `idx_nombre_cliente` en `clientes.nombre_cliente` |
| **Índices de BD** | Filtro por mora | `idx_mora` en `clientes.mora` |
| **Índices de BD** | Búsqueda combinada nombre+mora | `idx_nombre_mora` |
| **Índices de BD** | Presupuestos por cliente | `idx_presupuesto_id_cliente` |
| **Índices de BD** | Presupuestos por fecha | `idx_presupuesto_fecha` |
| **Índices de BD** | Ventas por fecha | `idx_ventas_fecha_venta` |
| **Índices de BD** | Puntos por cliente | `idx_puntos_id_cliente` |
| **Tipos de datos** | Fechas como `date`/`datetime`/`time` en lugar de `varchar` | Permite comparaciones nativas, rangos, ORDER BY eficiente |
| **Tipos de datos** | `decimal` en lugar de `double` para montos | Evita errores de precisión en cálculos monetarios |
| **ORM + connection pool** | JPA/Hibernate con HikariCP | Reemplaza JDBC manual sin pool ni prepared statements reutilizados |
| **Frontend SPA** | Sin recargas completas de página | Los reload pages (JSP) desaparecen |
| **Soft delete** | `disabled` en lugar de borrado + validación | Evita queries de validación al borrar |

---

## 9. Decisiones de Arquitectura

### 9.1 Stack Tecnológico

| Capa | Original (Struts) | Nuevo |
|---|---|---|
| **Framework backend** | Apache Struts 1.3.10 | Spring Boot 3.x |
| **Lenguaje backend** | Java 1.6 | Java 17+ |
| **ORM / acceso a datos** | JDBC manual (`JDatos`) | Spring Data JPA + Hibernate |
| **Migraciones BD** | Sin control de versión | Flyway |
| **Framework frontend** | JSP + HTML frameset + DisplayTag | React 19 + Material UI 7 |
| **Build frontend** | N/A (JSP en servidor) | Vite 8 |
| **Routing frontend** | Struts forwards (server-side) | React Router DOM 7 |
| **HTTP client** | N/A | Axios |
| **Comunicación** | Server-side rendering (JSP forward) | REST API JSON |
| **Autenticación** | Sin autenticación real | JWT (JSON Web Tokens) |
| **Documentación API** | Sin documentación | SpringDoc / Swagger UI |
| **Build backend** | Maven | Maven (mantenido) |
| **Base de datos** | MySQL 5.1 | MySQL 8.0 |
| **Charset** | latin1 / utf8mb3 mixto | utf8mb4 uniforme |

---

### 9.2 Autenticación: JWT

**Implementación en `precisionAppBE`:**
- Tabla `users`: `id_user`, `username`, `password` (bcrypt hash), `role`
- Clases: `JwtService`, `JwtAuthenticationFilter`, `SecurityConfig`, `AuthenticationService`
- Controller: `AuthController`, `LoginController`
- Flujo:
  ```
  POST /auth/login  { username, password }
  → Valida credenciales
  → Retorna JWT token
  → Cliente incluye token en header: Authorization: Bearer <token>
  → JwtAuthenticationFilter valida cada request
  ```
- Roles: definidos en enum/tabla `Role`
- Secret: configurable via variable de entorno `JWT_SECRET`

---

### 9.3 Separación Frontend / Backend

```
precisionAppBE/                                       ← Spring Boot REST API
  ruta: C:\...\Migracion_PrecisionApp\precisionAppBE
  puerto: 8080 (default)
  base de datos: precision_schema_v2
  autenticación: JWT

claudeCode/presision-app/                             ← React SPA
  ruta: C:\...\claudeCode\presision-app
  puerto: 5173 (Vite dev server)
  consume: API REST de precisionAppBE
  UI: Material UI 7
```

**CORS:** Configurado en `CorsConfig.java` para permitir requests del frontend.

**Comunicación:**
- JSON sobre HTTP/REST via Axios (`src/services/api.js`)
- No hay server-side rendering
- El frontend mantiene el JWT en `AuthContext` (React Context)
- Cada request lleva el token en el header `Authorization: Bearer <token>`
- Rutas protegidas con componente `PrivateRoute`

---

### 9.4 UI: Material UI (MUI)

- Versión: MUI 7 (`@mui/material ^7.3.9`, `@mui/icons-material ^7.3.9`)
- Motor de estilos: Emotion (`@emotion/react`, `@emotion/styled`)
- Reemplaza: JSP custom + DisplayTag grillas + HTML frameset
- Componentes clave en uso:
  - `DataGrid` → reemplaza las grillas de DisplayTag
  - `Dialog` / `Modal` → reemplaza los forwards a páginas separadas de ABM
  - `AppBar` + `Drawer` → reemplaza el frameset con `menu.jsp`
  - `TextField`, `Select`, `Autocomplete` → reemplaza form inputs de Struts
  - `Snackbar` / `Alert` → reemplaza el `error.jsp` para mensajes de error

---

### 9.5 Estructura del Proyecto Backend

```
precisionAppBE/
├── controller/          ← REST Controllers (un controller por módulo)
│   ├── AuthController
│   ├── ClienteController
│   ├── PresupuestoController
│   ├── TrabajosController
│   ├── CierresController
│   ├── VentasController
│   ├── MaterialesController
│   ├── VariosController
│   ├── ExtraccionesController
│   ├── MaquinasController
│   ├── EventsController
│   ├── PagosController
│   ├── UserController
│   ├── UtilsController
│   └── ExportController
├── services/            ← Lógica de negocio
├── repositories/        ← Spring Data JPA repositories
├── model/               ← Entidades JPA
├── dto/                 ← Data Transfer Objects (request/response)
│   └── response/        ← DTOs de respuesta específicos
├── security/            ← JWT, filtros, configuración de seguridad
│   ├── JwtService
│   ├── JwtAuthenticationFilter
│   ├── SecurityConfig
│   └── ApplicationConfig
├── configuration/       ← SwaggerConfig, CorsConfig
└── Mapper/              ← Mapeo entidad ↔ DTO
```

### 9.6 Estructura del Proyecto Frontend

```
presision-app/                    ← React 19 + Vite + MUI 7
├── src/
│   ├── App.jsx                   ← Rutas principales con React Router 7
│   ├── main.jsx
│   ├── context/
│   │   └── AuthContext.jsx       ← Estado de autenticación global (JWT)
│   ├── components/
│   │   ├── Layout.jsx            ← AppBar + Drawer (reemplaza frameset)
│   │   ├── PrivateRoute.jsx      ← Guard de rutas autenticadas
│   │   └── RegisterDialog.jsx
│   ├── pages/
│   │   ├── Login.jsx             ✅ Implementado
│   │   ├── Register.jsx          ✅ Implementado
│   │   ├── Placeholder.jsx       ← Usado para módulos pendientes
│   │   ├── clientes/             ✅ Implementado
│   │   ├── presupuestos/         ✅ Implementado
│   │   ├── trabajos/             ✅ Implementado
│   │   ├── usuarios/             ✅ Implementado
│   │   ├── calendarios/          ✅ Implementado
│   │   ├── materiales/           ✅ Implementado
│   │   └── parametros/           ✅ Implementado
│   └── services/
│       ├── api.js                ← Instancia Axios con interceptor de JWT
│       ├── authService.js
│       ├── clientesService.js
│       ├── materialesService.js
│       ├── presupuestosService.js
│       ├── trabajosService.js
│       ├── usersService.js
│       ├── utilsService.js
│       └── variosService.js
├── package.json
└── vite.config.js
```

**Rutas implementadas:**

| Ruta | Componente | Estado |
|---|---|---|
| `/login` | `Login` | ✅ |
| `/clientes` | `Clientes` | ✅ |
| `/clientes/:clienteId/presupuestos` | `Presupuestos` | ✅ |
| `/clientes/:clienteId/presupuestos/:presupuestoId/trabajos` | `Trabajos` | ✅ |
| `/usuarios` | `Usuarios` | ✅ |
| `/calendarios` | `Calendarios` | ✅ |
| `/materiales` | `Materiales` | ✅ |
| `/parametros` | `Parametros` | ✅ |
| `/ventas` | `Placeholder` | ❌ Pendiente |
| `/cierres` | `Placeholder` | ❌ Pendiente |

---

### 9.7 Decisiones de Diseño Tomadas

| Decisión | Descripción |
|---|---|
| **Flyway para migraciones** | Control de versión del schema. Cada cambio de BD es un script versionado en `db/migration/`. Nunca más cambios manuales. |
| **`decimal` para montos** | Reemplaza `double` en todos los campos monetarios. Evita errores de punto flotante en sumas de caja. |
| **`datetime` para fechas** | Reemplaza varchars. Permite queries de rango, ORDER BY, y GROUP BY nativos. |
| **FK explícitas** | Todas las relaciones tienen foreign keys declaradas. Garantiza integridad referencial a nivel BD. |
| **Soft delete** | Clientes y materiales tienen campo `disabled`. Los registros nunca se borran físicamente, preservando el historial. |
| **Catálogos normalizados** | `tipo_cliente`, `tipo_descuento`, `tipo_pago`, `medio_pago`, `tarjeta`, `superficies`, `maquinas` son tablas de referencia. Extensibles sin cambiar código. |
| **Modelo de pagos unificado** | `pago_presupuesto` y `pago_venta` reemplazan los flags simples de cobro. Un presupuesto puede tener múltiples pagos (seña + saldo). |
| **Auditoría de parámetros** | `varios_historial` guarda el historial completo de cambios a precio/minuto. Crítico para auditar por qué un presupuesto tiene un precio dado. |
| **Historial de puntos** | `control_puntos` mantiene el snapshot completo de puntos en cada transacción. |
| **No filesystem** | La generación de carpetas en disco Windows desaparece. Los archivos CAD serán gestionados de otra forma (pendiente definir: storage cloud / filesystem servidor). |
| **JWT stateless** | No hay sesiones HTTP. Cada request es autónomo con el token. Permite escalar horizontalmente. |
| **API-first** | El backend expone una API REST documentada con Swagger. El frontend es un consumidor más, igual que cualquier integración futura. |
