package dev.eduardo.chatbotsolvyx.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeRequestDTO {

    @NotBlank(message = "userId es obligatorio")
    private String userId;

    @NotBlank(message = "nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "mensaje es obligatorio")
    @Size(max = 600, message = "mensaje no puede superar los 600 caracteres")
    private String mensaje;
}
