package com.example.shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shop.entities.Reporte;
import com.example.shop.repositories.ReporteRepository;
import org.springframework.ui.Model;
import java.util.List;
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private ReporteRepository reporteRepository;
@GetMapping("/reportes")
    public String mostrarReportes(Model model) {
        List<Reporte> reportes = reporteRepository.findAll();
        model.addAttribute("reportes", reportes);
        return "admin/reportes";
    }
    @GetMapping
    public String panelAdmin() {
        return "admin";
    }
    
}
