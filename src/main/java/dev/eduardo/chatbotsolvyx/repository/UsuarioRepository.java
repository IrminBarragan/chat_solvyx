package dev.eduardo.chatbotsolvyx.repository;

import dev.eduardo.chatbotsolvyx.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}
