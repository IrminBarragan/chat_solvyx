package dev.eduardo.chatbotsolvyx.repository;

import dev.eduardo.chatbotsolvyx.entity.Historial;
import dev.eduardo.chatbotsolvyx.entity.Mensaje;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findByHistorialOrderByIdDesc(Historial historial, Pageable pageable);

    List<Mensaje> findByHistorialOrderByIdAsc(Historial historial);
}
