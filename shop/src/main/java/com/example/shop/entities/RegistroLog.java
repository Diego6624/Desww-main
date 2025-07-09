package com.example.shop.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "registro_log")
public class RegistroLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Long id;

    @Column(name = "action")
    private String accion;
    
    @Column(name = "fecha")
    private LocalDateTime fecha;
}
