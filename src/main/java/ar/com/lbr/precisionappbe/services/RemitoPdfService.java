package ar.com.lbr.precisionappbe.services;

import ar.com.lbr.precisionappbe.model.Cliente;
import ar.com.lbr.precisionappbe.model.Material;
import ar.com.lbr.precisionappbe.model.Presupuesto;
import ar.com.lbr.precisionappbe.model.TrabajoPresupuestado;
import ar.com.lbr.precisionappbe.repositories.ClienteRepository;
import ar.com.lbr.precisionappbe.repositories.MaterialRepository;
import ar.com.lbr.precisionappbe.repositories.PresupuestoRepository;
import ar.com.lbr.precisionappbe.repositories.TrabajoPresupuestadoRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RemitoPdfService {

    private static final String EMPRESA_NOMBRE = "PRECISION";
    private static final String EMPRESA_DIRECCION = "Independencia 1255 - Nva. Cba - 0351-156630999";
    private static final String LOCALIDAD_DEFAULT = "Córdoba";
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color HEADER_BG = new Color(220, 220, 220);
    private static final int MARGIN = 36;

    private final PresupuestoRepository presupuestoRepository;
    private final ClienteRepository clienteRepository;
    private final TrabajoPresupuestadoRepository trabajoRepository;
    private final MaterialRepository materialRepository;

    public RemitoPdfService(PresupuestoRepository presupuestoRepository,
            ClienteRepository clienteRepository,
            TrabajoPresupuestadoRepository trabajoRepository,
            MaterialRepository materialRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.clienteRepository = clienteRepository;
        this.trabajoRepository = trabajoRepository;
        this.materialRepository = materialRepository;
    }

    public byte[] generateRemito(Integer idPresupuesto) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presupuesto no encontrado"));
        Cliente cliente = clienteRepository.findById(presupuesto.getIdCliente())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        List<TrabajoPresupuestado> trabajos = trabajoRepository.findByIdPresupuesto(idPresupuesto);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, MARGIN, MARGIN, MARGIN, MARGIN);
        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();
            addHeader(doc, presupuesto);
            addClientData(doc, cliente);
            addTrabajosTable(doc, trabajos);
            addFooter(doc);
        } catch (DocumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generando PDF");
        } finally {
            doc.close();
        }
        return baos.toByteArray();
    }

    private void addHeader(Document doc, Presupuesto presupuesto) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{60f, 40f});
        table.addCell(buildHeaderLeft());
        table.addCell(buildHeaderRight(presupuesto));
        doc.add(table);
    }

    private PdfPCell buildHeaderLeft() {
        Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28);
        Font addrFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(10);
        cell.addElement(new Paragraph(EMPRESA_NOMBRE, logoFont));
        cell.addElement(new Paragraph(EMPRESA_DIRECCION, addrFont));
        return cell;
    }

    private PdfPCell buildHeaderRight(Presupuesto presupuesto) {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        PdfPTable inner = new PdfPTable(1);
        inner.setWidthPercentage(100);

        PdfPCell titleCell = centeredCell(new Phrase("REMITO", titleFont));
        titleCell.setBackgroundColor(HEADER_BG);
        inner.addCell(titleCell);
        inner.addCell(centeredCell(new Phrase("Fecha", labelFont)));
        inner.addCell(centeredCell(new Phrase(presupuesto.getFechaHoraPresupuesto().format(FECHA_FMT), valueFont)));
        inner.addCell(centeredCell(new Phrase("Nro: " + presupuesto.getId(), labelFont)));

        PdfPCell wrapper = new PdfPCell(inner);
        wrapper.setBorder(Rectangle.BOX);
        wrapper.setPadding(0);
        return wrapper;
    }

    private PdfPCell centeredCell(Phrase phrase) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(5);
        return cell;
    }

    private void addClientData(Document doc, Cliente cliente) throws DocumentException {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{25f, 75f});

        addClientRow(table, "Razon Social:", nullSafe(cliente.getNombreCliente()), labelFont, valueFont);
        addClientRow(table, "Domicilio:", "", labelFont, valueFont);
        addClientRow(table, "Localidad:", LOCALIDAD_DEFAULT, labelFont, valueFont);
        addClientRow(table, "CUIT:", nullSafe(cliente.getDniCliente()), labelFont, valueFont);
        addClientRow(table, "Telefono:", nullSafe(cliente.getTelefonoCliente()), labelFont, valueFont);

        doc.add(table);
    }

    private void addClientRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addTrabajosTable(Document doc, List<TrabajoPresupuestado> trabajos) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{8f, 15f, 18f, 13f, 18f, 14f, 14f});

        String[] headers = {"Nro", "Tipo", "Precio Sin Desc.", "Descuento", "Material", "Seña", "Pendiente"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.BOX);
            cell.setPadding(5);
            cell.setBackgroundColor(HEADER_BG);
            table.addCell(cell);
        }

        for (TrabajoPresupuestado t : trabajos) {
            BigDecimal sinDesc = nvl(t.getPrecioSinDescuento());
            BigDecimal descuento = nvl(t.getDescuento());
            BigDecimal pendiente = sinDesc.subtract(descuento);

            dataCell(table, String.valueOf(t.getId()), cellFont, Element.ALIGN_CENTER);
            dataCell(table, derivarTipo(t), cellFont, Element.ALIGN_LEFT);
            dataCell(table, formatMoney(sinDesc), cellFont, Element.ALIGN_RIGHT);
            dataCell(table, formatMoney(descuento), cellFont, Element.ALIGN_RIGHT);
            dataCell(table, resolverMaterial(t.getIdMateriales()), cellFont, Element.ALIGN_LEFT);
            dataCell(table, formatMoney(BigDecimal.ZERO), cellFont, Element.ALIGN_RIGHT);
            dataCell(table, formatMoney(pendiente), cellFont, Element.ALIGN_RIGHT);
        }

        doc.add(table);
    }

    private void dataCell(PdfPTable table, String value, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private void addFooter(Document doc) throws DocumentException {
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setPadding(8);
        cell.setMinimumHeight(80);
        cell.addElement(new Paragraph("Recibi Conforme:", boldFont));
        Paragraph firma = new Paragraph("\n\nFIRMA Y ACLARACION", normalFont);
        firma.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(firma);
        table.addCell(cell);

        doc.add(table);
    }

    private String resolverMaterial(Integer idMaterial) {
        if (idMaterial == null) {
            return "";
        }
        return materialRepository.findById(idMaterial)
                .map(Material::getMateriales)
                .orElse("");
    }

    private String derivarTipo(TrabajoPresupuestado t) {
        if (Boolean.TRUE.equals(t.getGrabado())) {
            return "Grabado";
        }
        if (Boolean.TRUE.equals(t.getCarteles())) {
            return "Carteles";
        }
        if (Boolean.TRUE.equals(t.getCortesEspeciales())) {
            return "Cortes Especiales";
        }
        return "Corte";
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    private String formatMoney(BigDecimal value) {
        return String.format("$ %.2f", value);
    }
}
