package com.example.shop.services;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;


public class PdfGenerator {
     public void generarResumenVentaConEstilo(OutputStream output, String nombreCliente, String direccion, String telefono,
                                             List<String[]> productos, double total, LocalDate fecha) throws Exception {

        Document document = new Document(PageSize.A4, 40, 40, 80, 40); // márgenes
        PdfWriter writer = PdfWriter.getInstance(document, output);
        document.open();

        // Colores
        BaseColor colorOro = new BaseColor(218, 165, 32);
        BaseColor grisClaro = new BaseColor(245, 245, 245);
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
        Font fontCabecera = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font fontCuerpo = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);

        // === Encabezado con logo ===
        Image logo = Image.getInstance("src/main/resources/static/images/logo_tokeinka.png"); // adapta el path
        logo.scaleToFit(120, 120);
        logo.setAlignment(Element.ALIGN_LEFT);
        document.add(logo);

        Paragraph titulo = new Paragraph("Resumen de Venta - Toke Inka", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);

        // Fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Paragraph fechaP = new Paragraph("Fecha: " + fecha.format(formatter), fontCuerpo);
        fechaP.setAlignment(Element.ALIGN_RIGHT);
        fechaP.setSpacingAfter(10f);
        document.add(fechaP);

        // === Datos del cliente ===
        PdfPTable clienteTable = new PdfPTable(2);
        clienteTable.setWidthPercentage(100);
        clienteTable.setWidths(new float[]{1f, 2.5f});
        clienteTable.setSpacingAfter(20f);

        clienteTable.addCell(getCell("Cliente:", fontCabecera, colorOro));
        clienteTable.addCell(getCell(nombreCliente, fontCuerpo, grisClaro));
        clienteTable.addCell(getCell("Dirección:", fontCabecera, colorOro));
        clienteTable.addCell(getCell(direccion, fontCuerpo, grisClaro));
        clienteTable.addCell(getCell("Teléfono:", fontCabecera, colorOro));
        clienteTable.addCell(getCell(telefono, fontCuerpo, grisClaro));

        document.add(clienteTable);

        // === Tabla de productos ===
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{4f, 1f, 1.5f});
        tabla.setSpacingAfter(15f);

        Stream.of("Producto", "Cantidad", "Precio").forEach(header -> {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontCabecera));
            cell.setBackgroundColor(colorOro);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            tabla.addCell(cell);
        });

        for (String[] prod : productos) {
            tabla.addCell(getCellTexto(prod[0], fontCuerpo));
            tabla.addCell(getCellTexto(prod[1], fontCuerpo));
            tabla.addCell(getCellTexto("S/. " + prod[2], fontCuerpo));
        }

        document.add(tabla);

        // === Total ===
        Paragraph totalP = new Paragraph("Total: S/. " + total, new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK));
        totalP.setAlignment(Element.ALIGN_RIGHT);
        totalP.setSpacingBefore(10f);
        document.add(totalP);

        // === Mensaje final ===
        Paragraph mensaje = new Paragraph("¡Gracias por confiar en Toke Inka!", new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY));
        mensaje.setAlignment(Element.ALIGN_CENTER);
        mensaje.setSpacingBefore(30f);
        document.add(mensaje);

        document.close();
        writer.close();
    }

    private PdfPCell getCell(String texto, Font font, BaseColor fondo) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(8);
        cell.setBackgroundColor(fondo);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell getCellTexto(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}
