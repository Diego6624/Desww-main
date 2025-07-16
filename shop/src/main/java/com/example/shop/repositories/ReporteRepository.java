package com.example.shop.repositories;

import com.example.shop.entities.Reporte;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
}