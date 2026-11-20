package com.pascualbravo.urgencias.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos del formulario de médico. Las reglas de formato (Bean Validation) pertenecen
 * al adaptador web; deben coincidir con docs/09-reglas-de-campos.md y con la plantilla.
 */
@Getter @Setter @NoArgsConstructor
public class MedicoForm {

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
}
