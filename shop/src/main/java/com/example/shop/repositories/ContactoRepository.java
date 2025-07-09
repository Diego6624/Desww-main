package com.example.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shop.entities.Contacto;

public interface ContactoRepository extends JpaRepository<Contacto, Long>{
    
}
