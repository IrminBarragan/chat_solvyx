package dev.eduardo.chatbotsolvyx.repository;

import dev.eduardo.chatbotsolvyx.entity.Historial;
import dev.eduardo.chatbotsolvyx.entity.StatusHistorial;
import dev.eduardo.chatbotsolvyx.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistorialRepository extends JpaRepository<Historial, Long> {

    Optional<Historial> findFirstByUsuarioAndStatusOrderByFechaInicioDesc(Usuario usuario, StatusHistorial status);
}
