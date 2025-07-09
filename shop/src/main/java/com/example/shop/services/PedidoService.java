package com.example.shop.services;

import com.example.shop.entities.Pedido;
import com.example.shop.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

   
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    
    public Pedido crearPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    
    public Pedido actualizarPedido(Long id, Pedido pedidoActualizado) {
        if (pedidoRepository.existsById(id)) {
            pedidoActualizado.setIdPedido(id); 
            return pedidoRepository.save(pedidoActualizado);
        }
        return null; 
    }

   
    public boolean eliminarPedido(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
