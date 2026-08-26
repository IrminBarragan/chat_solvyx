package dev.eduardo.chatbotsolvyx.repository;

import dev.eduardo.chatbotsolvyx.entity.PalabraProhibida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PalabraProhibidaRepository extends JpaRepository<PalabraProhibida, Long> {

    List<PalabraProhibida> findByActivoTrue();
}
