package com.example.shop.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.shop.entities.Producto;
import com.example.shop.entities.TallaProducto;
import com.example.shop.services.ProductoService;
import com.example.shop.services.TallaProductoService;
import com.example.shop.services.TallaService;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/talla-producto")
public class AdminTallaProductoController {

    @Autowired
    private TallaProductoService tallaProductoService;

    @Autowired
    private TallaService tallaService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String listarTallaPorProductos(Model model) {
        model.addAttribute("tallasProductos", tallaProductoService.listarTodos());
        return "admin-tallaProducto";
    }

    @GetMapping("/nuevo/{idProducto}")
    public String formularioTallasProducto(@PathVariable Long idProducto, Model model) {
        Optional<Producto> productoOpt = productoService.obtenerProductoPorId(idProducto);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            model.addAttribute("producto", producto);

            TallaProducto tallaProducto = new TallaProducto();
            tallaProducto.setProducto(producto);

            model.addAttribute("tallas", tallaService.listarTallas());
            model.addAttribute("tallaProducto", tallaProducto);
            return "tallaProducto-form";
        } else {
            return "redirect:/admin/talla-producto";
        }
    }

    @PostMapping("/guardar")
    public String guardarTallaProducto(@ModelAttribute TallaProducto tallaProducto) {
        tallaProductoService.guardarTallaProducto(tallaProducto);
        return "redirect:/admin/talla-producto";
    }

    @GetMapping("/editar/{idTallaProducto}")
    public String editarTallaProducto(@PathVariable Long idTallaProducto, Model model) {
        TallaProducto tp = tallaProductoService.obtenerPorId(idTallaProducto);
        if (tp == null) {
            throw new IllegalArgumentException("ID inválido: " + idTallaProducto);
        }

        model.addAttribute("tallaProducto", tp);
        model.addAttribute("producto", tp.getProducto());
        model.addAttribute("tallas", tallaService.listarTallas());
        return "tallaProducto-form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        tallaProductoService.eliminarTallaProducto(id);
        return "redirect:/admin/talla-producto";
    }

}
