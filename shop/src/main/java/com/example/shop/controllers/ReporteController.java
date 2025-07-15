package com.example.shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.shop.entities.Reporte;
import com.example.shop.services.ReporteService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {


    
    @Autowired
    private ReporteService reporteService;

    // Mostrar la página de reportes
    @GetMapping
    public String mostrarReportes(Model model) {
        try {
            // Cargar reportes del último mes por defecto
            LocalDate fechaFin = LocalDate.now();
            LocalDate fechaInicio = fechaFin.minusMonths(1);
            
            List<Reporte> reportes = reporteService.obtenerReportesPorFecha(fechaInicio, fechaFin);
            model.addAttribute("reportes", reportes);
            
            return "admin/reportes";
        } catch (Exception e) {
            System.err.println("❌ Error al cargar reportes: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    // API para obtener reportes con filtros
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Reporte>> obtenerReportesApi(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        try {
            List<Reporte> reportes;
            
            if (fechaInicio != null && fechaFin != null) {
                reportes = reporteService.obtenerReportesPorFecha(fechaInicio, fechaFin);
            } else {
                // Si no hay fechas, devolver los últimos 30 días
                LocalDate fin = LocalDate.now();
                LocalDate inicio = fin.minusDays(30);
                reportes = reporteService.obtenerReportesPorFecha(inicio, fin);
            }
            
            return ResponseEntity.ok(reportes);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener reportes: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // API para obtener estadísticas
    @GetMapping("/api/estadisticas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        try {
            Map<String, Object> estadisticas = reporteService.obtenerEstadisticas(fechaInicio, fechaFin);
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener estadísticas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // API para productos más vendidos
    @GetMapping("/api/productos-mas-vendidos")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerProductosMasVendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "10") int limite) {
        
        try {
            List<Map<String, Object>> productos = reporteService.obtenerProductosMasVendidos(fechaInicio, fechaFin, limite);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener productos más vendidos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // API para ventas por período
    @GetMapping("/api/ventas-por-periodo")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerVentasPorPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "dia") String agrupacion) {
        
        try {
            List<Map<String, Object>> ventas = reporteService.obtenerVentasPorPeriodo(fechaInicio, fechaFin, agrupacion);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener ventas por período: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
}