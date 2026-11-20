package com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA de la tabla "medicos". Solo se usa dentro del adaptador de persistencia;
 * el resto del sistema trabaja con el modelo de dominio Medico.
 */
@Entity
@Table(name = "medicos")
@Getter @Setter @NoArgsConstructor
public class MedicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 80, nullable = false)
    private String especialidad;

    @Column(length = 100, nullable = false)
    private String correo;

    private boolean disponible = true;
}
