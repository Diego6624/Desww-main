package com.example.shop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shop.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreProductoContainingIgnoreCaseOrCategoria_NombreCategoriaContainingIgnoreCase(
            String nombre, String categoria);

}