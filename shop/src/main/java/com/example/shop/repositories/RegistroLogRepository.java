package com.example.shop.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shop.entities.RegistroLog;

public interface RegistroLogRepository extends JpaRepository<RegistroLog, Long> {
    List<RegistroLog> findByAccion(String accion);
    List<RegistroLog> findByFechaBetween(LocalDateTime start, LocalDateTime end);
}