package dev.eduardo.chatbotsolvyx.repository;

import dev.eduardo.chatbotsolvyx.entity.NotaUsuario;
import dev.eduardo.chatbotsolvyx.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotaUsuarioRepository extends JpaRepository<NotaUsuario, Long> {

    Optional<NotaUsuario> findByUsuario(Usuario usuario);
}
