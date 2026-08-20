import os
import shutil
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

pdf_path = r"c:\Users\LEandro\Documents\development\MigracionPrecision\precisionAppBE\scripts\MANUAL_SCRIPTS_PRECISIONAPP.pdf"
prod_copy = r"C:\precision_app\MANUAL_SCRIPTS_PRECISIONAPP.pdf"

doc = SimpleDocTemplate(
    pdf_path,
    pagesize=letter,
    rightMargin=40,
    leftMargin=40,
    topMargin=40,
    bottomMargin=40
)

styles = getSampleStyleSheet()

primary_color = colors.HexColor('#1565C0')
secondary_color = colors.HexColor('#0D47A1')
text_dark = colors.HexColor('#212121')
text_muted = colors.HexColor('#546E7A')
bg_light = colors.HexColor('#F5F7FA')

title_style = ParagraphStyle(
    'DocTitle',
    parent=styles['Normal'],
    fontName='Helvetica-Bold',
    fontSize=22,
    leading=26,
    textColor=primary_color,
    spaceAfter=6
)

subtitle_style = ParagraphStyle(
    'DocSubtitle',
    parent=styles['Normal'],
    fontName='Helvetica',
    fontSize=11,
    leading=15,
    textColor=text_muted,
    spaceAfter=12
)

h1_style = ParagraphStyle(
    'SectionH1',
    parent=styles['Normal'],
    fontName='Helvetica-Bold',
    fontSize=13,
    leading=17,
    textColor=secondary_color,
    spaceBefore=12,
    spaceAfter=5
)

body_style = ParagraphStyle(
    'BodyTextCustom',
    parent=styles['Normal'],
    fontName='Helvetica',
    fontSize=9,
    leading=13,
    textColor=text_dark,
    spaceAfter=5
)

table_header_style = ParagraphStyle(
    'TableHeader',
    parent=styles['Normal'],
    fontName='Helvetica-Bold',
    fontSize=9,
    leading=12,
    textColor=colors.white
)

table_cell_style = ParagraphStyle(
    'TableCell',
    parent=styles['Normal'],
    fontName='Helvetica',
    fontSize=8.5,
    leading=11.5,
    textColor=text_dark
)

story = []

# Title & Subtitle
story.append(Paragraph('PrecisionApp - Manual de Scripts Operativos', title_style))
story.append(Paragraph('Documentacion tecnica y operativa de scripts de administracion, respaldo y despliegue en produccion', subtitle_style))
story.append(HRFlowable(width='100%', thickness=2, color=primary_color, spaceAfter=12))

# Overview Table
summary_data = [
    [Paragraph('Script', table_header_style), Paragraph('Funcion Principal', table_header_style), Paragraph('Frecuencia de Uso', table_header_style)],
    [Paragraph('<b>iniciar_precision_app.bat</b>', table_cell_style), Paragraph('Verifica BD, inicia Backend Spring Boot y Frontend NGINX.', table_cell_style), Paragraph('Diaria (apertura)', table_cell_style)],
    [Paragraph('<b>detener_precision_app.bat</b>', table_cell_style), Paragraph('Backup preventivo de BD + detencion limpia de Java y NGINX.', table_cell_style), Paragraph('Diaria (cierre)', table_cell_style)],
    [Paragraph('<b>backup_db.bat / .ps1</b>', table_cell_style), Paragraph('Dump MySQL, compresion ZIP, copia a Drive y rotacion mensual.', table_cell_style), Paragraph('Automatico / Manual', table_cell_style)],
    [Paragraph('<b>actualizar_produccion.bat</b>', table_cell_style), Paragraph('Pull de GitHub (main), compilacion BE/FE y reinicio total.', table_cell_style), Paragraph('Bajo demanda (releases)', table_cell_style)],
    [Paragraph('<b>actualizar_be.bat</b>', table_cell_style), Paragraph('Compila unicamente el Backend Spring Boot y copia el .jar.', table_cell_style), Paragraph('Mantenimiento BE', table_cell_style)],
    [Paragraph('<b>actualizar_fe.bat</b>', table_cell_style), Paragraph('Compila unicamente el Frontend React y copia carpeta dist.', table_cell_style), Paragraph('Mantenimiento FE', table_cell_style)]
]

t_summary = Table(summary_data, colWidths=[150, 260, 120])
t_summary.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), primary_color),
    ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
    ('TOPPADDING', (0, 0), (-1, -1), 4),
    ('LEFTPADDING', (0, 0), (-1, -1), 6),
    ('RIGHTPADDING', (0, 0), (-1, -1), 6),
    ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, bg_light]),
    ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CFD8DC'))
]))

story.append(t_summary)
story.append(Spacer(1, 10))

# 1. Iniciar
story.append(Paragraph('1. iniciar_precision_app.bat (Encendido Diario)', h1_style))
story.append(Paragraph('<b>Ubicacion:</b> C:\\precision_app\\iniciar_precision_app.bat', body_style))
story.append(Paragraph('<b>Proposito:</b> Script principal para iniciar la plataforma al comenzar la jornada laboral.', body_style))
story.append(Paragraph('• <b>1. Validacion de Base de Datos:</b> Verifica la existencia de precision_v2 y precisionschema. Si esta vacia, importa el archivo .sql mas reciente en C:\\precision_app\\.<br/>'
                       '• <b>2. Variables de Entorno:</b> Define puerto 10080, claves JWT, credenciales de base de datos y politicas CORS.<br/>'
                       '• <b>3. Inicio Backend:</b> Ejecuta el jar de Spring Boot en una ventana dedicada con logs en vivo y escritura simultanea a backend.log.<br/>'
                       '• <b>4. Inicio Frontend:</b> Inicia el servidor web NGINX sirviendo la aplicacion en http://localhost:10081.', body_style))
story.append(Spacer(1, 8))

# 2. Detener
story.append(Paragraph('2. detener_precision_app.bat (Apagado Diario y Backup Preventivo)', h1_style))
story.append(Paragraph('<b>Ubicacion:</b> C:\\precision_app\\detener_precision_app.bat', body_style))
story.append(Paragraph('<b>Proposito:</b> Apagado ordenado del sistema al finalizar la jornada laboral.', body_style))
story.append(Paragraph('• <b>1. Backup Preventivo:</b> Llama automaticamente a backup_db.bat para resguardar la base de datos de inmediato antes de cerrar.<br/>'
                       '• <b>2. Detencion Backend:</b> Cierra el proceso java.exe y la ventana de consola asociada.<br/>'
                       '• <b>3. Detencion Frontend:</b> Cierra el proceso nginx.exe.', body_style))
story.append(Spacer(1, 8))

# Page Break
story.append(PageBreak())

# 3. Backup
story.append(Paragraph('3. backup_db.bat / backup_db.ps1 (Respaldo y Rotacion)', h1_style))
story.append(Paragraph('<b>Ubicacion:</b> C:\\precision_app\\backup_db.bat y C:\\precision_app\\backup_db.ps1', body_style))
story.append(Paragraph('<b>Proposito:</b> Generar respaldos consistentes, comprimirlos a .zip, rotar el almacenamiento y sincronizar con la nube.', body_style))
story.append(Paragraph('• <b>mysqldump InnoDB:</b> Utiliza --single-transaction, --quick, --routines y --triggers para garantizar un dump transaccional sin bloqueos.<br/>'
                       '• <b>Compresion ZIP:</b> Comprime el archivo SQL a precision_v2_YYYY_MM_DD_HHMMSS.zip (reduccion de ~85% del espacio).<br/>'
                       '• <b>Copia a la Nube:</b> Si se configura la variable CLOUD_DIR en backup_db.bat (ej: Google Drive o OneDrive), realiza una copia automatica.<br/>'
                       '• <b>Politica de Retencion Mensual:</b><br/>'
                       '&nbsp;&nbsp;&nbsp;&nbsp;- <i>Mes en curso:</i> Se conservan todos los backups diarios.<br/>'
                       '&nbsp;&nbsp;&nbsp;&nbsp;- <i>Meses anteriores:</i> Se preserva unicamente el ultimo backup de cada mes (cierre mensual) y se eliminan los backups intermedios antiguos de forma automatica.', body_style))
story.append(Spacer(1, 8))

# 4. Actualizar Produccion
story.append(Paragraph('4. actualizar_produccion.bat (Actualizacion desde GitHub Main)', h1_style))
story.append(Paragraph('<b>Ubicacion:</b> C:\\precision_app\\actualizar_produccion.bat', body_style))
story.append(Paragraph('<b>Proposito:</b> Descargar, compilar y desplegar la version mas reciente de la rama main en produccion con un solo clic.', body_style))
story.append(Paragraph('1. Detiene los servicios en ejecucion (detener_precision_app.bat).<br/>'
                       '2. Ejecuta git checkout main y git pull origin main en el repositorio de Backend.<br/>'
                       '3. Compila el Backend con gradlew.bat bootJar y copia precisionAppBE.jar a C:\\precision_app\\.<br/>'
                       '4. Ejecuta git checkout main y git pull origin main en el repositorio de Frontend.<br/>'
                       '5. Compila el Frontend con npm run build y copia la carpeta dist a C:\\precision_app\\frontend\\.<br/>'
                       '6. Vuelve a iniciar todos los servicios automaticamente (iniciar_precision_app.bat).', body_style))
story.append(Spacer(1, 8))

# 5. Actualizaciones individuales
story.append(Paragraph('5. actualizar_be.bat y actualizar_fe.bat (Compilacion Manual)', h1_style))
story.append(Paragraph('<b>Proposito:</b> Scripts auxiliares para recompilar y copiar exclusivamente una sola capa en entornos de prueba o mantenimiento especifico.', body_style))
story.append(Paragraph('• <b>actualizar_be.bat:</b> Compila el Backend y reemplaza C:\\precision_app\\precisionAppBE.jar.<br/>'
                       '• <b>actualizar_fe.bat:</b> Compila el Frontend y reemplaza C:\\precision_app\\frontend\\.', body_style))
story.append(Spacer(1, 12))

story.append(HRFlowable(width='100%', thickness=1, color=colors.HexColor('#CFD8DC'), spaceAfter=8))
story.append(Paragraph('PrecisionApp - Sistema de Gestion y Corte Laser | Documento generado para Produccion', ParagraphStyle('FooterNote', parent=styles['Normal'], fontName='Helvetica-Oblique', fontSize=8, textColor=text_muted)))

doc.build(story)
print("PDF generado exitosamente:", pdf_path)
shutil.copyfile(pdf_path, prod_copy)
print("Copia guardada en:", prod_copy)
