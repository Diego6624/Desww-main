package com.example.shop.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence. Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table (name = "administrador")
public class Administrador {
    
    @Id
    @GeneratedValue (strategy= GenerationType. IDENTITY)
    @Column(name = "idAdministrador")
    private long idadministrador;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
}
