package com.example.shop.controllers;

import com.example.shop.entities.Contacto;
import com.example.shop.repositories.ContactoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactoController {

    @Autowired
    private ContactoRepository contactoRepository;

    @GetMapping("/contactanos")
    public String mostrarFormularioContacto(Model model) {
        model.addAttribute("contacto", new Contacto());
        return "contactanos";
    }

    @PostMapping("/contactanos")
    public String procesarFormularioContacto(@ModelAttribute("Contacto") Contacto contacto) {
        contactoRepository.save(contacto); 
        return "redirect:/contactanos?exito";
    }
}
