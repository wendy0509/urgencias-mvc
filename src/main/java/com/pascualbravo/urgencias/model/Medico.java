package com.pascualbravo.urgencias.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicos")
@Getter @Setter @NoArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
    @Pattern(regexp = "[\\p{L} .'-]*", message = "El nombre solo debe contener letras")
    private String nombre;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 80, message = "Máximo 80 caracteres")
    @Pattern(regexp = "[\\p{L} .'-]*", message = "La especialidad solo debe contener letras")
    private String especialidad;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String correo;

    private boolean disponible = true;

    @OneToMany(mappedBy = "medico")
    private List<Paciente> pacientes = new ArrayList<>();
}