# PrecisionApp - Manual de Scripts Operativos

Documentación técnica y operativa de los scripts de administración, respaldo y despliegue en producción.

---

## 📋 Resumen de Scripts

| Script | Función Principal | Frecuencia de Uso |
| :--- | :--- | :--- |
| **`iniciar_precision_app.bat`** | Verifica BD, inicia Backend Spring Boot y Frontend NGINX. | Diaria (al abrir el local) |
| **`detener_precision_app.bat`** | Backup preventivo de BD + detención limpia de Java y NGINX. | Diaria (al cerrar el local) |
| **`backup_db.bat` / `.ps1`** | Dump MySQL, compresión ZIP, copia a Drive y rotación mensual. | Automático / Manual |
| **`actualizar_produccion.bat`** | Pull de GitHub (`main`), compilación BE/FE y reinicio total. | Bajo demanda (nuevas versiones) |
| **`actualizar_be.bat`** | Compila únicamente el Backend Spring Boot y copia el `.jar`. | Mantenimiento BE |
| **`actualizar_fe.bat`** | Compila únicamente el Frontend React y copia la carpeta `dist`. | Mantenimiento FE |

---

## 1. `iniciar_precision_app.bat` (Encendido Diario)
* **Ubicación:** `C:\precision_app\iniciar_precision_app.bat`
* **Propósito:** Script principal que el operador ejecuta al comenzar la jornada laboral.
* **Flujo de Ejecución:**
  1. **Validación de Base de Datos:** Verifica la existencia de `precision_v2` y `precisionschema`. Si la base no existe, importa el archivo `.sql` más reciente en `C:\precision_app\`.
  2. **Variables de Entorno:** Configura puerto `10080`, contraseñas, secretos JWT y orígenes CORS.
  3. **Inicio de Backend:** Lanza el proceso Java Spring Boot (`precisionAppBE.jar`) en una ventana de consola dedicada titulada *"Backend - Spring Boot"*, redirigiendo la salida simultáneamente a `C:\precision_app\backend.log`.
  4. **Inicio de Frontend:** Lanza el servidor web NGINX sirviendo la aplicación en `http://localhost:10081`.

---

## 2. `detener_precision_app.bat` (Apagado Diario y Backup Preventivo)
* **Ubicación:** `C:\precision_app\detener_precision_app.bat`
* **Propósito:** Apagado limpio y seguro de todos los servicios al finalizar el día de trabajo.
* **Flujo de Ejecución:**
  1. **Backup Preventivo:** Llama automáticamente a `backup_db.bat` para resguardar la base de datos de inmediato antes de cerrar.
  2. **Detención de Backend:** Cierra el proceso `java.exe` y la ventana de consola *"Backend - Spring Boot"*.
  3. **Detención de Frontend:** Cierra el proceso `nginx.exe`.
  4. **Cierre Automático:** Notifica el éxito y se cierra en 3 segundos.

---

## 3. `backup_db.bat` / `backup_db.ps1` (Respaldo y Rotación de BD)
* **Ubicación:** `C:\precision_app\backup_db.bat` y `C:\precision_app\backup_db.ps1`
* **Propósito:** Generar copias de seguridad consistentes, comprimirlas a `.zip`, sincronizarlas con la nube y rotar el almacenamiento.
* **Características y Políticas:**
  * **mysqldump InnoDB:** Utiliza `--single-transaction`, `--quick`, `--routines` y `--triggers` para no interrumpir operaciones ni bloquear lecturas.
  * **Compresión Automática:** Comprime el archivo SQL a `precision_v2_YYYY_MM_DD_HHMMSS.zip` (reducción de ~85% del espacio).
  * **Sincronización con la Nube:** Permite configurar la variable `CLOUD_DIR` en `backup_db.bat` (ej. Google Drive o OneDrive) para hacer una copia automática a la nube.
  * **Política de Retención Mensual:**
    * *Mes en curso:* Se conservan todos los backups diarios generados.
    * *Meses anteriores:* Se preserva únicamente el último backup de cada mes (cierre mensual) y se eliminan los backups intermedios antiguos de forma automática.

---

## 4. `actualizar_produccion.bat` (Actualización desde GitHub Main)
* **Ubicación:** `C:\precision_app\actualizar_produccion.bat`
* **Propósito:** Desplegar en producción la versión más reciente de la rama `main` con un solo clic.
* **Flujo de Ejecución:**
  1. Detiene los servicios en ejecución (`detener_precision_app.bat`).
  2. Realiza `git checkout main` y `git pull origin main` en el repositorio Backend.
  3. Compila el Backend con `gradlew.bat bootJar` y copia `precisionAppBE.jar` a `C:\precision_app\`.
  4. Realiza `git checkout main` y `git pull origin main` en el repositorio Frontend.
  5. Compila el Frontend con `npm run build` y copia `dist` a `C:\precision_app\frontend\`.
  6. Vuelve a iniciar todos los servicios automáticamente (`iniciar_precision_app.bat`).

---

## 5. `actualizar_be.bat` y `actualizar_fe.bat` (Compilación Manual)
* **Propósito:** Scripts auxiliares para recompilar y copiar exclusivamente una sola capa en entornos de prueba o mantenimiento específico.
  * **`actualizar_be.bat`:** Compila el Backend y reemplaza `C:\precision_app\precisionAppBE.jar`.
  * **`actualizar_fe.bat`:** Compila el Frontend y reemplaza `C:\precision_app\frontend\`.
