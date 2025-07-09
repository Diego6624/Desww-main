package com.example.shop.repositories;

import com.example.shop.entities.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    
    Administrador findByUsername(String username);
}
