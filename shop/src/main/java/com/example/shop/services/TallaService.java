package com.example.shop.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.shop.entities.Talla;
import com.example.shop.repositories.TallaRepository;

@Service
public class TallaService {
    
    @Autowired
    private TallaRepository tallaRepository;

    public List<Talla> listarTallas() {
        return tallaRepository.findAll();
    }

    public void guardarTalla(Talla talla) {
        tallaRepository.save(talla);
    }

    public void eliminarTalla(Long id) {
        tallaRepository.deleteById(id);
    }

    public Optional<Talla> obtenerTallaPorId(Long id) {
        return tallaRepository.findById(id);
    }
}
