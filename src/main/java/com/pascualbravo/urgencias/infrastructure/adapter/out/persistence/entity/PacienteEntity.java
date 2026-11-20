package com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.entity;

import com.pascualbravo.urgencias.domain.model.EstadoPaciente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad JPA de la tabla "pacientes". Conserva las mismas columnas que la versión MVC
 * para que la base de datos existente siga funcionando sin migraciones.
 */
@Entity
@Table(name = "pacientes")
@Getter @Setter @NoArgsConstructor
public class PacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 20, nullable = false, unique = true)
    private String documento;

    private Integer edad;

    private String motivoConsulta;

    private Integer nivelGravedad;

    private String alergias;

    private String prioridad;

    private LocalDateTime fechaLlegada;

    @Enumerated(EnumType.STRING)
    private EstadoPaciente estado = EstadoPaciente.EN_ESPERA;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private MedicoEntity medico;
}
