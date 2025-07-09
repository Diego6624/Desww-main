package com.example.shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shop.entities.Talla;
import com.example.shop.services.TallaService;

@Controller
@RequestMapping("/admin/tallas")
public class AdminTallaController {
    
    @Autowired
    private TallaService tallaService;

    @GetMapping
    public String listarTallas(Model model) {
        model.addAttribute("tallas", tallaService.listarTallas());
        return "admin-tallas";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioTalla(Model model) {
        model.addAttribute("talla", new Talla());
        return "tallas_form";
    }

    @PostMapping("/guardar")
    public String guardarTalla(@ModelAttribute Talla talla) {
        tallaService.guardarTalla(talla);
        return "redirect:/admin/tallas";
    }

    @GetMapping("/editar/{id}")
    public String editarTalla(@PathVariable Long id, Model model) {
        Talla talla = tallaService.obtenerTallaPorId(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("talla", talla);
        return "tallas_form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarTalla(@PathVariable Long id) {
        tallaService.eliminarTalla(id);
        return "redirect:/admin/tallas";
    }

}
