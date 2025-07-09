package com.example.shop.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private long idUsuario;

    @Column(name = "nombre_usuario")
    private String nombre;

    @Column(name = "apellido_usuario")
    private String apellido;

    @Column(name = "email")
    private String email;

    @Column(name = "contraseña")
    private String contraseña;

    // @Column(name = "telefono")
    // private String telefono;

    // @Column(name = "dni")
    // private String dni;

    // @Column(name = "fecha_nacimiento")
    // private String fechaNacimiento;

    // @Column(name = "genero")
    // private String genero;

    // @Column(name = "ciudad")
    // private String ciudad;

    // @Column(name = "departamento")
    // private String departamento;

    // @Column(name = "pais")
    // private String pais;

    // @Column(name = "codigo_postal")
    // private String codigoPostal;

    // @Column(name = "direccion")
    // private String direccion;

    @Column(name = "rol")
    private String rol;

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;
}