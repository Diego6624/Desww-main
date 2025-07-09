package com.example.shop.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shop.entities.TallaProducto;

public interface TallaProductoRepository extends JpaRepository<TallaProducto, Long>{
    List<TallaProducto> findByProducto_IdProducto(Long idProducto);
    Optional<TallaProducto> findByProducto_IdProductoAndTalla_IdTalla(Long idProducto, Long idTalla);
}
