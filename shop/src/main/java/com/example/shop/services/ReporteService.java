package com.example.shop.services;

import com.example.shop.entities.Reporte;
import com.example.shop.repositories.ReporteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    // Obtener reportes por rango de fechas
    public List<Reporte> obtenerReportesPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return reporteRepository.findAll();
        }
        return reporteRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    // Obtener todos los reportes
    public List<Reporte> obtenerTodosLosReportes() {
        return reporteRepository.findAll();
    }

    // Guardar un nuevo reporte
    public Reporte guardarReporte(Reporte reporte) {
        return reporteRepository.save(reporte);
    }

    // Obtener estadísticas generales
    public Map<String, Object> obtenerEstadisticas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Reporte> reportes = obtenerReportesPorFecha(fechaInicio, fechaFin);
        
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Total de ventas
        estadisticas.put("totalVentas", reportes.size());
        
        // Total de ingresos
        double totalIngresos = reportes.stream()
            .mapToDouble(Reporte::getTotal)
            .sum();
        estadisticas.put("totalIngresos", totalIngresos);
        
        // Clientes únicos
        Set<String> clientesUnicos = reportes.stream()
            .map(Reporte::getNombreCliente)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        estadisticas.put("clientesUnicos", clientesUnicos.size());
        
        // Total de productos vendidos
        int totalProductos = reportes.stream()
            .mapToInt(this::contarProductosEnReporte)
            .sum();
        estadisticas.put("totalProductos", totalProductos);
        
        return estadisticas;
    }

    // Obtener productos más vendidos
    public List<Map<String, Object>> obtenerProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin, int limite) {
        List<Reporte> reportes = obtenerReportesPorFecha(fechaInicio, fechaFin);
        
        Map<String, Integer> contadorProductos = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        
        reportes.forEach(reporte -> {
            if (reporte.getProductos() != null && !reporte.getProductos().isEmpty()) {
                try {
                    // Intentar parsear como JSON
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> productos = mapper.readValue(reporte.getProductos(), List.class);
                    
                    for (Map<String, Object> producto : productos) {
                        String nombre = (String) producto.get("title");
                        Integer cantidad = ((Number) producto.get("quantity")).intValue();
                        
                        if (nombre != null && cantidad != null) {
                            contadorProductos.put(nombre, 
                                contadorProductos.getOrDefault(nombre, 0) + cantidad);
                        }
                    }
                } catch (Exception e) {
                    // Si falla el JSON, intentar con separador de punto y coma (legacy)
                    String[] productos = reporte.getProductos().split(";");
                    for (String producto : productos) {
                        String nombreProducto = producto.trim();
                        if (!nombreProducto.isEmpty()) {
                            contadorProductos.put(nombreProducto, 
                                contadorProductos.getOrDefault(nombreProducto, 0) + 1);
                        }
                    }
                }
            }
        });
        
        return contadorProductos.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limite)
            .map(entry -> {
                Map<String, Object> producto = new HashMap<>();
                producto.put("nombre", entry.getKey());
                producto.put("cantidad", entry.getValue());
                return producto;
            })
            .collect(Collectors.toList());
    }

    // Obtener ventas por período
    public List<Map<String, Object>> obtenerVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin, String agrupacion) {
        List<Reporte> reportes = obtenerReportesPorFecha(fechaInicio, fechaFin);
        
        Map<String, Double> ventasPorPeriodo = new HashMap<>();
        DateTimeFormatter formatter;
        
        // Definir formato según agrupación
        switch (agrupacion.toLowerCase()) {
            case "mes":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                break;
            case "año":
                formatter = DateTimeFormatter.ofPattern("yyyy");
                break;
            case "semana":
                formatter = DateTimeFormatter.ofPattern("yyyy-'W'w");
                break;
            default: // día
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                break;
        }
        
        reportes.forEach(reporte -> {
            String periodo = reporte.getFecha().format(formatter);
            ventasPorPeriodo.put(periodo, 
                ventasPorPeriodo.getOrDefault(periodo, 0.0) + reporte.getTotal());
        });
        
        return ventasPorPeriodo.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                Map<String, Object> venta = new HashMap<>();
                venta.put("periodo", entry.getKey());
                venta.put("total", entry.getValue());
                return venta;
            })
            .collect(Collectors.toList());
    }

    // Obtener reporte por ID
    public Optional<Reporte> obtenerReportePorId(Long id) {
        return reporteRepository.findById(id);
    }

    // Eliminar reporte
    public void eliminarReporte(Long id) {
        reporteRepository.deleteById(id);
    }

    // Obtener ventas por cliente
    public List<Map<String, Object>> obtenerVentasPorCliente(LocalDate fechaInicio, LocalDate fechaFin, int limite) {
        List<Reporte> reportes = obtenerReportesPorFecha(fechaInicio, fechaFin);
        
        Map<String, Double> ventasPorCliente = new HashMap<>();
        
        reportes.forEach(reporte -> {
            String cliente = reporte.getNombreCliente();
            if (cliente != null && !cliente.isEmpty()) {
                ventasPorCliente.put(cliente, 
                    ventasPorCliente.getOrDefault(cliente, 0.0) + reporte.getTotal());
            }
        });
        
        return ventasPorCliente.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limite)
            .map(entry -> {
                Map<String, Object> cliente = new HashMap<>();
                cliente.put("nombre", entry.getKey());
                cliente.put("total", entry.getValue());
                return cliente;
            })
            .collect(Collectors.toList());
    }

    // Método privado para contar productos en un reporte
    private int contarProductosEnReporte(Reporte reporte) {
        if (reporte.getProductos() == null || reporte.getProductos().isEmpty()) {
            return 0;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> productos = mapper.readValue(reporte.getProductos(), List.class);
            
            return productos.stream()
                .mapToInt(producto -> {
                    Object quantity = producto.get("quantity");
                    if (quantity instanceof Number) {
                        return ((Number) quantity).intValue();
                    }
                    return 0;
                })
                .sum();
        } catch (Exception e) {
            // Fallback para formato legacy
            return reporte.getProductos().split(";").length;
        }
    }
}