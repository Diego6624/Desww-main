package com.example.shop.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shop.entities.Usuario;
import com.example.shop.services.UsuarioService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listarUsuarios(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Usuario> usuarioPage = usuarioService.listarUsuariosPaginados(pageable);
        model.addAttribute("usuarios", usuarioPage.getContent());
        model.addAttribute("usuariosPage", usuarioPage);
        model.addAttribute("paginaActual", page);
        return "admin-usuario";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("rol", List.of("USER", "ADMIN"));
        return "usuario_form";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        if (!usuario.getContraseña().startsWith("$2a$")) { // Evita encriptar si ya está encriptada
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
    }
        usuarioService.guardarUsuario(usuario);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id).orElseThrow();
        model.addAttribute("usuario", usuario);
        model.addAttribute("rol", List.of("USER", "ADMIN"));
        return "usuario_form";
    }

    @GetMapping("eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/admin/usuarios";
    }
}
