package dev.eduardo.chatbotsolvyx.dto.response;

import dev.eduardo.chatbotsolvyx.entity.StatusHistorial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CerrarSesionResponseDTO {

    private Long historialId;
    private StatusHistorial status;
}
