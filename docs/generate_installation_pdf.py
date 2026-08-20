import os
import shutil
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

pdf_path = r"c:\Users\LEandro\Documents\development\MigracionPrecision\precisionAppBE\docs\GUIA_INSTALACION_PRODUCCION.pdf"
prod_copy = r"C:\precision_app\GUIA_INSTALACION_PRODUCCION.pdf"

doc = SimpleDocTemplate(
    pdf_path,
    pagesize=letter,
    rightMargin=36,
    leftMargin=36,
    topMargin=36,
    bottomMargin=36
)

styles = getSampleStyleSheet()

primary_color = colors.HexColor('#1565C0')
secondary_color = colors.HexColor('#0D47A1')
accent_color = colors.HexColor('#E65100')
text_dark = colors.HexColor('#212121')
text_muted = colors.HexColor('#546E7A')
bg_light = colors.HexColor('#F5F7FA')
bg_code = colors.HexColor('#ECEFF1')

title_style = ParagraphStyle(
    'DocTitle',
    parent=styles['Normal'],
    fontName='Helvetica-Bold',
    fontSize=20,
    leading=24,
    textColor=primary_color,
    spaceAfter=4
)

subtitle_style = ParagraphStyle(
    'DocSubtitle',
    parent=styles['Normal'],
    fontName='Helvetica',
    fontSize=10,
    leading=14,
    textColor=text_muted,
    spaceAfter=10
)

h1_style = ParagraphStyle(
    'SectionH1',
    parent=styles['Normal'],
    fontName='Helvetica-Bold',
    fontSize=12,
    leading=16,
    textColor=secondary_color,
    spaceBefore=10,
    spaceAfter=4
)

body_style = ParagraphStyle(
    'BodyTextCustom',
    parent=styles['Normal'],
    fontName='Helvetica',
    fontSize=8.5,
    leading=12,
    textColor=text_dark,
    spaceAfter=4
)

table_header_style = ParagraphStyle(
    'TableHeader',
    parent=styles['Normal'],
    fontName='Helvetica-Bold',
    fontSize=8.5,
    leading=11.5,
    textColor=colors.white
)

table_cell_style = ParagraphStyle(
    'TableCell',
    parent=styles['Normal'],
    fontName='Helvetica',
    fontSize=8,
    leading=10.5,
    textColor=text_dark
)

callout_style = ParagraphStyle(
    'CalloutText',
    parent=styles['Normal'],
    fontName='Helvetica-Bold',
    fontSize=8.5,
    leading=12,
    textColor=accent_color
)

story = []

# Title & Subtitle
story.append(Paragraph('PrecisionApp v2 - Guia de Instalacion en Produccion', title_style))
story.append(Paragraph('Manual de despliegue en Windows con coexistencia de Java 8 (Legacy), NGINX, MySQL y Cloudflare Tunnel', subtitle_style))
story.append(HRFlowable(width='100%', thickness=2, color=primary_color, spaceAfter=8))

# 1. Arquitectura y Puertos
story.append(Paragraph('1. Arquitectura de Coexistencia y Distribucion de Puertos', h1_style))
story.append(Paragraph('Para garantizar que la <b>aplicacion existente (Java 8)</b> y <b>PrecisionApp v2</b> funcionen simultaneamente sin ningun conflicto en la misma maquina, se definio la siguiente arquitectura:', body_style))

arch_data = [
    [Paragraph('Componente', table_header_style), Paragraph('App Legacy (Existente)', table_header_style), Paragraph('PrecisionApp v2 (Nueva)', table_header_style)],
    [Paragraph('<b>Version de Java</b>', table_cell_style), Paragraph('Java 8 (JRE / JDK 1.8)', table_cell_style), Paragraph('<b>Java 21 LTS</b> (Eclipse Temurin)', table_cell_style)],
    [Paragraph('<b>Puerto Backend</b>', table_cell_style), Paragraph('Puerto 8080 (o standalone)', table_cell_style), Paragraph('<b>Puerto 10080</b> (Spring Boot)', table_cell_style)],
    [Paragraph('<b>Servidor Web Frontend</b>', table_cell_style), Paragraph('IIS / Tomcat / Standalone', table_cell_style), Paragraph('<b>NGINX en Puerto 10081</b> (C:\\nginx)', table_cell_style)],
    [Paragraph('<b>Base de Datos MySQL</b>', table_cell_style), Paragraph('Base precisionschema (MySQL 8)', table_cell_style), Paragraph('Base <b>precision_v2</b> (MySQL 8)', table_cell_style)],
    [Paragraph('<b>Acceso Publico / Remoto</b>', table_cell_style), Paragraph('Red Local o IP directa', table_cell_style), Paragraph('<b>Cloudflare Tunnel</b> (precision.lbrebolini.net)', table_cell_style)]
]

t_arch = Table(arch_data, colWidths=[130, 190, 220])
t_arch.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), primary_color),
    ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 3),
    ('TOPPADDING', (0, 0), (-1, -1), 3),
    ('LEFTPADDING', (0, 0), (-1, -1), 5),
    ('RIGHTPADDING', (0, 0), (-1, -1), 5),
    ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, bg_light]),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CFD8DC'))
]))
story.append(t_arch)
story.append(Spacer(1, 8))

# 2. Coexistencia Java
story.append(Paragraph('2. Instalacion y Coexistencia de Java (Java 8 y Java 21)', h1_style))
story.append(Paragraph('<b>ADVERTENCIA:</b> La aplicacion anterior depende de Java 8. No desinstalar Java 8 ni sobreescribir la variable PATH global si la aplicacion legacy la utiliza.', callout_style))
story.append(Paragraph('1. Descargar <b>Eclipse Temurin OpenJDK 21 LTS</b> (Windows x64 .msi) desde adoptium.net.<br/>'
                       '2. Instalar en la ruta por defecto: <code>C:\\Program Files\\Eclipse Adoptium\\jdk-21*</code>.<br/>'
                       '3. <i>iniciar_precision_app.bat</i> ejecutara Java 21 especificamente para PrecisionApp, garantizando que el sistema operativo y la aplicacion legacy mantengan su ejecucion en Java 8 intacta.', body_style))
story.append(Spacer(1, 8))

# 3. Estructura de Carpetas
story.append(Paragraph('3. Estructura de Directorios en Produccion (C:\\precision_app\\)', h1_style))
story.append(Paragraph('Crear la carpeta principal <b>C:\\precision_app\\</b> con sus subcarpetas:<br/>'
                       '• <b>C:\\precision_app\\</b> : precisionAppBE.jar, Dump20260616.sql, backend.log y scripts .bat.<br/>'
                       '• <b>C:\\precision_app\\frontend\\</b> : Archivos compilados de la interfaz web React (dist).<br/>'
                       '• <b>C:\\precision_app\\backups\\</b> : Carpeta de almacenamiento de backups diarios comprimidos en .zip.<br/>'
                       '• <b>C:\\precision_app\\archivos_usuarios\\</b> : Carpeta destinada a guardar archivos de diseno/corte de clientes.<br/>'
                       '• <b>C:\\nginx\\</b> : Instalacion del servidor web NGINX y configuracion de proxy.', body_style))
story.append(Spacer(1, 8))

# 4. Servidor Web NGINX
story.append(Paragraph('4. Instalacion y Configuracion de NGINX', h1_style))
story.append(Paragraph('1. Descargar NGINX para Windows y descomprimir en <b>C:\\nginx\\</b>.<br/>'
                       '2. En <b>C:\\nginx\\conf\\nginx.conf</b>, configurar el servidor escuchando en el puerto <b>10081</b>:<br/>'
                       '&nbsp;&nbsp;• <code>root C:/precision_app/frontend;</code> (sirve la SPA de React con <code>try_files $uri $uri/ /index.html;</code>).<br/>'
                       '&nbsp;&nbsp;• <code>location /api/ { proxy_pass http://localhost:10080/api/; }</code> (redirige las llamadas REST al Backend Spring Boot).', body_style))
story.append(Spacer(1, 8))

# Page Break
story.append(PageBreak())

# 5. Base de Datos MySQL
story.append(Paragraph('5. Base de Datos MySQL (precision_v2 y precisionschema)', h1_style))
story.append(Paragraph('1. Ambas aplicaciones utilizan el servicio local de MySQL 8.0 en el puerto 3306.<br/>'
                       '2. Colocar el dump <code>Dump20260616.sql</code> en <code>C:\\precision_app\\</code>.<br/>'
                       '3. Al ejecutar <code>iniciar_precision_app.bat</code> por primera vez, el script creara la base <b>precision_v2</b> e importara automaticamente el dump en <b>precisionschema</b> si fuera necesario, ejecutando las migraciones Flyway de forma transparente.', body_style))
story.append(Spacer(1, 8))

# 6. Cloudflare Tunnel
story.append(Paragraph('6. Configuracion de Cloudflare Tunnel (Acceso Remoto HTTPS)', h1_style))
story.append(Paragraph('Cloudflare Tunnel publica la aplicacion en <b>https://precision.lbrebolini.net</b> con cifrado SSL sin abrir puertos en el router:', body_style))
story.append(Paragraph('• <b>Paso 1:</b> Descargar <code>cloudflared.exe</code> desde GitHub de Cloudflare y guardarlo en <code>C:\\Program Files\\cloudflared\\</code>.<br/>'
                       '• <b>Paso 2:</b> Abrir PowerShell como Administrador y autenticar con: <code>cloudflared tunnel login</code>.<br/>'
                       '• <b>Paso 3:</b> Crear el tunel: <code>cloudflared tunnel create precision-tunnel</code> (generara un UUID y archivo .json).<br/>'
                       '• <b>Paso 4:</b> Crear <code>C:\\Users\\&lt;Usuario&gt;\\.cloudflared\\config.yml</code> con el mapeo:<br/>'
                       '&nbsp;&nbsp;&nbsp;&nbsp;<code>tunnel: &lt;UUID&gt;</code><br/>'
                       '&nbsp;&nbsp;&nbsp;&nbsp;<code>credentials-file: C:\\Users\\&lt;Usuario&gt;\\.cloudflared\\&lt;UUID&gt;.json</code><br/>'
                       '&nbsp;&nbsp;&nbsp;&nbsp;<code>ingress:</code><br/>'
                       '&nbsp;&nbsp;&nbsp;&nbsp;<code>&nbsp;&nbsp;- hostname: precision.lbrebolini.net</code><br/>'
                       '&nbsp;&nbsp;&nbsp;&nbsp;<code>&nbsp;&nbsp;&nbsp;&nbsp;service: http://localhost:10081</code><br/>'
                       '&nbsp;&nbsp;&nbsp;&nbsp;<code>&nbsp;&nbsp;- service: http_status:404</code><br/>'
                       '• <b>Paso 5:</b> Rutar el dominio DNS: <code>cloudflared tunnel route dns precision-tunnel precision.lbrebolini.net</code>.<br/>'
                       '• <b>Paso 6:</b> Instalar e iniciar como Servicio de Windows: <code>cloudflared service install</code> y <code>Start-Service cloudflared</code>.', body_style))
story.append(Spacer(1, 8))

# 7. Sincronizacion de Backup en la Nube
story.append(Paragraph('7. Configuracion de Backup y Carpetas de Usuarios', h1_style))
story.append(Paragraph('• <b>Carpeta de Clientes:</b> Configurar <code>C:\\precision_app\\archivos_usuarios\\</code> para almacenar presupuestos y archivos importados.<br/>'
                       '• <b>Google Drive / OneDrive:</b> En <code>C:\\precision_app\\backup_db.bat</code>, definir la variable <code>CLOUD_DIR</code> con la ruta de la carpeta sincronizada en la PC (ej: <code>C:\\Users\\LEandro\\Google Drive\\BackupsPrecision</code>). Al detener la app, se enviara automaticamente una copia de seguridad comprimida a la nube.', body_style))
story.append(Spacer(1, 8))

# 8. Puesta en Marcha
story.append(Paragraph('8. Puesta en Marcha y Verificacion', h1_style))
story.append(Paragraph('1. <b>Iniciar:</b> Doble clic en <code>C:\\precision_app\\iniciar_precision_app.bat</code>.<br/>'
                       '2. <b>Verificacion Local:</b> Ingresar a <code>http://localhost:10081</code> en el navegador.<br/>'
                       '3. <b>Verificacion Publica:</b> Ingresar a <code>https://precision.lbrebolini.net</code> desde cualquier dispositivo.<br/>'
                       '4. <b>Apagado y Backup:</b> Doble clic en <code>C:\\precision_app\\detener_precision_app.bat</code> y verificar la creacion del ZIP en <code>C:\\precision_app\\backups\\</code>.', body_style))
story.append(Spacer(1, 10))

story.append(HRFlowable(width='100%', thickness=1, color=colors.HexColor('#CFD8DC'), spaceAfter=6))
story.append(Paragraph('PrecisionApp v2 - Documentacion de Despliegue en Servidor de Produccion', ParagraphStyle('FooterNote', parent=styles['Normal'], fontName='Helvetica-Oblique', fontSize=7.5, textColor=text_muted)))

doc.build(story)
print("PDF generado:", pdf_path)
shutil.copyfile(pdf_path, prod_copy)
print("Copia en produccion:", prod_copy)
