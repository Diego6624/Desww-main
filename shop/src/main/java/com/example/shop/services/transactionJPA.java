package com.example.shop.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.shop.entities.Producto;
import com.example.shop.entities.RegistroLog;
import com.example.shop.repositories.ProductoRepository;
import com.example.shop.repositories.RegistroLogRepository;

import jakarta.transaction.Transactional;

@Service
public class transactionJPA {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private RegistroLogRepository logRepository;

    @Transactional
    public void crearProducto(Producto producto) {
        productoRepository.save(producto); // Se guarda el producto

        RegistroLog log = new RegistroLog();
        log.setAccion("Producto creado: " + producto.getNombreProducto());
        log.setFecha(LocalDateTime.now());

        logRepository.save(log); // Se guarda el log
    }
}
