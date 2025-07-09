package com.example.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shop.entities.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar detalles de pedido por ID de pedido o producto
}