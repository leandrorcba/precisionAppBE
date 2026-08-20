# Guía de Instalación y Despliegue en Producción - PrecisionApp

Guía paso a paso para la instalación de PrecisionApp v2 en un servidor / PC con Windows que convive con una aplicación existente en Java 8 y MySQL.

---

## 🏗️ 1. Arquitectura y Puertos de Producción

Para garantizar que la **aplicación existente (Legacy en Java 8)** y **PrecisionApp v2** funcionen en simultáneo sin conflictos, se utiliza la siguiente distribución de puertos y bases de datos:

| Componente | Aplicación Legacy (Existente) | PrecisionApp v2 (Nueva) |
| :--- | :--- | :--- |
| **Versión de Java** | Java 8 (JRE/JDK 1.8) | Java 21 (Eclipse Temurin JDK 21) |
| **Puerto de Backend** | Puerto 8080 (o standalone) | **Puerto 10080** |
| **Servidor Web Frontend** | IIS / Tomcat / Standalone | **NGINX en Puerto 10081** (`C:\nginx\`) |
| **Base de Datos MySQL** | Base `precisionschema` | Base **`precision_v2`** (coexisten en MySQL 8.0) |
| **Acceso Público** | Red Local o IP directa | **Cloudflare Tunnel (`precision.lbrebolini.net`)** |

---

## ☕ 2. Instalación y Coexistencia de Java (Java 8 y Java 21)

> ⚠️ **IMPORTANTE:** La aplicación anterior depende de Java 8. **No debemos desinstalar Java 8 ni sobreescribir la variable `JAVA_HOME` global de Windows si la app legacy la utiliza.**

1. Descargar **Eclipse Temurin OpenJDK 21 (LTS)** para Windows x64:
   * Sitio oficial: `https://adoptium.net/temurin/releases/?version=21`
   * Instalador: `OpenJDK21U-jdk_x64_windows_hotspot_21.x.x.msi`.
2. Durante la instalación:
   * Ruta de instalación predeterminada: `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot\`.
   * **Desmarcar** la opción de agregar al `PATH` global si se desea que Java 8 siga siendo el comando `java` predeterminado por consola.
3. En `iniciar_precision_app.bat`, PrecisionApp buscará automáticamente Java 21 en `C:\Program Files\Eclipse Adoptium\jdk-21*` o en la variable `JAVA21_HOME`.

---

## 📁 3. Creación de la Estructura de Directorios

En la raíz del disco `C:\`, crear la siguiente estructura de carpetas:

```text
C:\
├── nginx\                               # Servidor Web NGINX
│   ├── conf\
│   │   └── nginx.conf                   # Configuración del proxy
│   └── nginx.exe
│
└── precision_app\                       # Directorio principal de la aplicación
    ├── backend.log                      # Registro de logs en tiempo real
    ├── precisionAppBE.jar               # Ejecutable Spring Boot
    ├── Dump20260616.sql                 # Dump inicial de la base de datos
    ├── iniciar_precision_app.bat        # Script de inicio diario
    ├── detener_precision_app.bat        # Script de apagado y backup
    ├── backup_db.bat / .ps1             # Script de respaldo y rotación
    ├── actualizar_produccion.bat        # Script de actualización desde main
    │
    ├── backups\                         # Carpeta donde se guardan los ZIPs de backup
    ├── frontend\                        # Build compilado de React (HTML, CSS, JS)
    └── archivos_usuarios\               # Carpeta para archivos de clientes y cortes
```

---

## 🌐 4. Instalación y Configuración de NGINX

1. Descargar **NGINX para Windows** (versión estable) desde `https://nginx.org/en/download.html`.
2. Descomprimir el contenido en **`C:\nginx\`** (debe quedar `C:\nginx\nginx.exe`).
3. Reemplazar el archivo **`C:\nginx\conf\nginx.conf`** con la siguiente configuración:

```nginx
worker_processes 1;

events {
    worker_connections 1024;
}

http {
    include mime.types;
    default_type application/octet-stream;
    sendfile on;
    keepalive_timeout 65;

    server {
        listen 10081;
        server_name localhost;

        # Directorio del Frontend compilado de React
        root C:/precision_app/frontend;
        index index.html;

        # Soporte para React Router (SPA)
        location / {
            try_files $uri $uri/ /index.html;
        }

        # Redirección de llamadas de API al Backend Spring Boot
        location /api/ {
            proxy_pass http://localhost:10080/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        error_page 500 502 503 504 /50x.html;
        location = /50x.html {
            root html;
        }
    }
}
```

---

## 🗄️ 5. Configuración de Base de Datos MySQL

1. Ambas aplicaciones utilizan el mismo servicio de **MySQL 8.0** en el puerto `3306`.
2. Colocar el archivo dump inicial (ej. `Dump20260616.sql`) en `C:\precision_app\`.
3. Al ejecutar `iniciar_precision_app.bat` por primera vez:
   * Creará automáticamente la base de datos `precision_v2`.
   * Verificará si `precisionschema` existe; si no existe, importará automáticamente el dump `Dump20260616.sql`.
   * Spring Boot ejecutará las migraciones Flyway (V1 a V29) sobre `precision_v2`.

---

## ☁️ 6. Configuración de Cloudflare Tunnel (Acceso Seguro)

El túnel de Cloudflare permite acceder de forma pública y cifrada con HTTPS (`https://precision.lbrebolini.net`) sin abrir puertos en el router ni requerir IP pública fija.

### Paso 1: Instalar cloudflared
1. Descargar `cloudflared-windows-amd64.msi` o `.exe` desde:
   `https://github.com/cloudflare/cloudflared/releases/latest`
2. Instalarlo o guardarlo en `C:\Program Files\cloudflared\cloudflared.exe`.

### Paso 2: Autenticación
Abrir PowerShell como Administrador y ejecutar:
```powershell
cloudflared tunnel login
```
*(Se abrirá el navegador para seleccionar la cuenta y el dominio `lbrebolini.net`)*.

### Paso 3: Crear el Túnel
```powershell
cloudflared tunnel create precision-tunnel
```
*(Esto genera un ID de túnel y un archivo de credenciales JSON en `C:\Users\<Usuario>\.cloudflared\<UUID>.json`)*.

### Paso 4: Crear el archivo de configuración `config.yml`
Crear el archivo `C:\Users\<Usuario>\.cloudflared\config.yml` con el siguiente contenido:

```yaml
tunnel: <UUID_DEL_TUNEL>
credentials-file: C:\Users\<Usuario>\.cloudflared\<UUID_DEL_TUNEL>.json

ingress:
  - hostname: precision.lbrebolini.net
    service: http://localhost:10081
  - service: http_status:404
```

### Paso 5: Asociar el DNS al Túnel
```powershell
cloudflared tunnel route dns precision-tunnel precision.lbrebolini.net
```

### Paso 6: Instalar Cloudflare Tunnel como Servicio de Windows
Para que el túnel inicie automáticamente cada vez que la PC enciende (sin necesidad de iniciar sesión):
```powershell
cloudflared service install
Start-Service cloudflared
```

---

## 👥 7. Configuración de Carpetas de Usuarios y Archivos de Trabajo

En la PC de producción:
1. Crear la carpeta **`C:\precision_app\archivos_usuarios\`** (o asignar la ruta en red compartida donde los operadores guardan los archivos `.dxf`, `.dwg` o `.pdf` de los clientes).
2. Si se utiliza sincronización de backups en la nube:
   * Instalar la aplicación de escritorio de **Google Drive** o **OneDrive**.
   * En `C:\precision_app\backup_db.bat`, configurar la variable `CLOUD_DIR`:
     ```batch
     set "CLOUD_DIR=C:\Users\<Usuario>\Google Drive\BackupsPrecision"
     ```

---

## 🚀 8. Puesta en Marcha y Verificación Final

1. **Iniciar la aplicación:**
   * Doble clic en `C:\precision_app\iniciar_precision_app.bat`.
   * Se abrirá la ventana de log del Backend y se iniciará NGINX.
2. **Verificación Local:**
   * Abrir el navegador e ingresar a `http://localhost:10081`.
   * Iniciar sesión con usuario `leandror` o `admin`.
3. **Verificación Pública / Remota:**
   * Abrir desde cualquier dispositivo `https://precision.lbrebolini.net`.
4. **Verificación de Backup y Apagado:**
   * Doble clic en `C:\precision_app\detener_precision_app.bat`.
   * Verificar que en `C:\precision_app\backups\` se haya generado el archivo `precision_v2_YYYY_MM_DD_HHMMSS.zip`.
