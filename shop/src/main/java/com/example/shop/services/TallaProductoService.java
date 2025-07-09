package com.example.shop.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.shop.entities.TallaProducto;
import com.example.shop.repositories.TallaProductoRepository;

@Service
public class TallaProductoService {

    @Autowired
    private TallaProductoRepository tallaProductoRepository;

    public List<TallaProducto> listarTodos() {
        return tallaProductoRepository.findAll();
    }

    public List<TallaProducto> listarTallaPorProductos(Long idProducto) {
        return tallaProductoRepository.findByProducto_IdProducto(idProducto);
    }

    public void guardarTallaProducto(TallaProducto tallaProducto) {
        // Solo para nuevos registros sin ID
        if (tallaProducto.getIdTallaProducto() == null) {
            Optional<TallaProducto> existente = tallaProductoRepository
                    .findByProducto_IdProducto(tallaProducto.getProducto().getIdProducto())
                    .stream()
                    .filter(tp -> tp.getTalla().getIdTalla().equals(tallaProducto.getTalla().getIdTalla()))
                    .findFirst();

            if (existente.isPresent()) {
                TallaProducto tpExistente = existente.get();
                tpExistente.setStock(tallaProducto.getStock());
                tallaProductoRepository.save(tpExistente);
                return;
            }
        }

        // Si es edición (ya tiene ID) o no hay duplicado
        tallaProductoRepository.save(tallaProducto);
    }

    public Optional<TallaProducto> obtenerPorTallaYProducto(Long idProducto, Long idTalla) {
        return tallaProductoRepository.findByProducto_IdProducto(idProducto).stream()
                .filter(tp -> tp.getTalla().getIdTalla().equals(idTalla))
                .findFirst();
    }

    public void eliminarTallaProducto(Long id) {
        tallaProductoRepository.deleteById(id);
    }

    public TallaProducto obtenerPorId(Long id) {
        return tallaProductoRepository.findById(id).orElse(null);
    }
}
