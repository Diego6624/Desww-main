package com.example.shop.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shop.entities.Producto;
import com.example.shop.entities.TallaProducto;
import com.example.shop.repositories.ProductoRepository;
import com.example.shop.services.ProductoService;
import com.example.shop.services.TallaProductoService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    
    @Autowired
    private final ProductoService productoService;

    @Autowired
    private final TallaProductoService tallaProductoService;

    public ProductoController(ProductoService productoService, ProductoRepository productoRepository, 
            TallaProductoService tallaProductoService) {
        this.productoService = productoService;
        this.productoRepository = productoRepository;
        this.tallaProductoService = tallaProductoService;
    }

    @GetMapping
    public String listarProductos(Model model) {
        List<Producto> productos = productoService.listarProductos();
        model.addAttribute("productos", productos);
        return "productos";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);
        return producto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/detalle/{id}")
    public String detalleProducto(@PathVariable Long id, Model model) {
        Optional<Producto> productoOptional = productoService.obtenerProductoPorId(id);

        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            List<TallaProducto> tallas = tallaProductoService.listarTallaPorProductos(producto.getIdProducto());
            model.addAttribute("producto", producto);
            model.addAttribute("tallas", tallas); 

            return "detalle-producto";
        } else {
            return "redirect:/error404";
        }
    }

    @GetMapping("/filtrar")
    public String filtrarProductos(@RequestParam(required = false) String filtro, Model model) {
        List<Producto> productos;
        if (filtro != null && !filtro.isEmpty()) {
            productos = productoRepository
                    .findByNombreProductoContainingIgnoreCaseOrCategoria_NombreCategoriaContainingIgnoreCase(filtro,
                            filtro);
        } else {
            productos = productoRepository.findAll();
        }
        model.addAttribute("productos", productos);
        return "productos";
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return productoService.crearProducto(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id,
            @RequestBody Producto productoActualizado) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, productoActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}