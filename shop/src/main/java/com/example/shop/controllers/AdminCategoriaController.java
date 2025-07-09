package com.example.shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shop.entities.Categoria;
import com.example.shop.services.CategoriaService;

@Controller
@RequestMapping("/admin/categorias")
public class AdminCategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listarCategorias(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Categoria> categoriasPage = categoriaService.listarCategoriasPaginadas(pageable);
        model.addAttribute("categorias", categoriasPage.getContent());
        model.addAttribute("categoriasPage", categoriasPage);
        model.addAttribute("paginaActual", page);
        return "admin-categoria";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categoria_form";
    }

    @PostMapping("/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria) {
        categoriaService.guardarCategoria(categoria);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        categoriaService.obtenerCategoriaPorId(id).ifPresent(c -> model.addAttribute("categoria", c));
        return "categoria_form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return "redirect:/admin/categorias";
    }
}
