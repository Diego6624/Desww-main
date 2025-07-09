package com.example.shop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/resumen")
public class ResumenController {
    
    @GetMapping
    public String mostrarResumen() {
        return "resumen"; // Retorna la vista resumen.html
    }
}