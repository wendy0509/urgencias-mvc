package com.pascualbravo.urgencias.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacientes")
@Getter @Setter @NoArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 5, max = 20, message = "El documento debe tener entre 5 y 20 caracteres")
    @Column(unique = true)
    private String documento;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 120, message = "La edad máxima es 120")
    private Integer edad;

    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(max = 255)
    private String motivoConsulta;

    @NotNull(message = "La gravedad es obligatoria")
    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer nivelGravedad;

    @Size(max = 255)
    private String alergias;            // RF20

    private String prioridad;           // calculada (RF02)

    private LocalDateTime fechaLlegada;

    @Enumerated(EnumType.STRING)
    private EstadoPaciente estado = EstadoPaciente.EN_ESPERA;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;
}