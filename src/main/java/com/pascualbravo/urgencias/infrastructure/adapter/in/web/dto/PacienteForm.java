package com.pascualbravo.urgencias.infrastructure.adapter.in.web.dto;

import com.pascualbravo.urgencias.domain.model.EstadoPaciente;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos del formulario de paciente. La relación con el médico viaja como medicoId;
 * el servicio de aplicación se encarga de resolverla.
 */
@Getter @Setter @NoArgsConstructor
public class PacienteForm {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre admite máximo 100 caracteres")
    @Pattern(regexp = "[\\p{L} .'-]*", message = "El nombre solo debe contener letras")
    private String nombre;

    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 5, max = 20, message = "El documento debe tener entre 5 y 20 dígitos")
    @Pattern(regexp = "\\d*", message = "El documento solo debe contener números")
    private String documento;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 120, message = "La edad máxima es 120")
    private Integer edad;

    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(max = 255, message = "Máximo 255 caracteres")
    private String motivoConsulta;

    @NotNull(message = "La gravedad es obligatoria")
    @Min(value = 1, message = "Mínimo 1")
    @Max(value = 5, message = "Máximo 5")
    private Integer nivelGravedad;

    @Size(max = 255, message = "Máximo 255 caracteres")
    private String alergias;

    private EstadoPaciente estado = EstadoPaciente.EN_ESPERA;

    @NotNull(message = "Debe seleccionar un médico")
    private Long medicoId;
}
