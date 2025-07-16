package com.example.shop.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.example.shop.entities.Reporte;
import com.example.shop.repositories.ReporteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/pago")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private ReporteRepository reporteRepository;

    @PostMapping(value = "/procesar", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> procesarPago(@RequestBody Map<String, Object> datos) {
        try {
            System.out.println("✅ Datos recibidos: " + datos);

            // Agregar fecha actual si no viene en los datos
            if (!datos.containsKey("fecha")) {
                datos.put("fecha", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            // Calcular total si no viene en los datos
            if (!datos.containsKey("total")) {
                double total = calcularTotal(datos);
                datos.put("total", total);
            }

            // prueba
            Reporte reporte = new Reporte();
            reporte.setNombreCliente((String) datos.get("nombre"));
            reporte.setDireccion((String) datos.get("direccion"));
            reporte.setTelefono((String) datos.get("telefono"));
            reporte.setFecha(LocalDate.now());
            reporte.setTotal(Double.parseDouble(datos.get("total").toString()));

            // Convertir lista de productos a JSON
            ObjectMapper mapper = new ObjectMapper();
            String productosJson = mapper.writeValueAsString(datos.get("productos"));
            reporte.setProductos(productosJson);

            // Guardar en base de datos
            reporteRepository.save(reporte);

            byte[] pdfBytes = generarComprobantePDF(datos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "comprobante_pago.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ Error al procesar pago: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @SuppressWarnings("unchecked")
    private byte[] generarComprobantePDF(Map<String, Object> datos) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, baos);

        documento.open();

        try {
            // Encabezado
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            Font fontTexto = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

            // Título
            Paragraph titulo = new Paragraph("COMPROBANTE DE PAGO", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(new Paragraph(" ", fontTexto)); // Espacio

            // Información del cliente
            documento.add(new Paragraph("DATOS DEL CLIENTE", fontSubtitulo));
            documento.add(new Paragraph("Fecha: " + datos.get("fecha"), fontTexto));
            documento.add(new Paragraph("Nombre: " + datos.get("nombre"), fontTexto));
            documento.add(new Paragraph("Dirección: " + datos.get("direccion"), fontTexto));
            documento.add(new Paragraph("Teléfono: " + datos.get("telefono"), fontTexto));
            documento.add(new Paragraph(" ", fontTexto)); // Espacio

            // Detalle de productos
            documento.add(new Paragraph("DETALLE DE PRODUCTOS", fontSubtitulo));
            List<Map<String, Object>> productos = (List<Map<String, Object>>) datos.get("productos");

            double totalCalculado = 0;

            for (Map<String, Object> prod : productos) {
                String nombre = (String) prod.get("title");
                Integer cantidad = ((Number) prod.get("quantity")).intValue();
                Double precio = ((Number) prod.get("unit_price")).doubleValue();
                double subtotal = cantidad * precio;
                totalCalculado += subtotal;

                String linea = String.format("• %s | Cantidad: %d | Precio: S/. %.2f | Subtotal: S/. %.2f",
                        nombre, cantidad, precio, subtotal);
                documento.add(new Paragraph(linea, fontTexto));
            }

            documento.add(new Paragraph(" ", fontTexto)); // Espacio

            // Total
            Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Paragraph total = new Paragraph(String.format("TOTAL A PAGAR: S/. %.2f", totalCalculado), fontTotal);
            total.setAlignment(Element.ALIGN_RIGHT);
            documento.add(total);

            documento.add(new Paragraph(" ", fontTexto)); // Espacio
            documento.add(new Paragraph("¡Gracias por su compra!", fontSubtitulo));

        } finally {
            documento.close();
        }

        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private double calcularTotal(Map<String, Object> datos) {
        List<Map<String, Object>> productos = (List<Map<String, Object>>) datos.get("productos");
        double total = 0;

        for (Map<String, Object> prod : productos) {
            Integer cantidad = ((Number) prod.get("quantity")).intValue();
            Double precio = ((Number) prod.get("unit_price")).doubleValue();
            total += cantidad * precio;
        }

        return total;
    }

}