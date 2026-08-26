package dev.eduardo.chatbotsolvyx.service;

import dev.eduardo.chatbotsolvyx.dto.deepseek.DeepSeekMessage;
import dev.eduardo.chatbotsolvyx.entity.Mensaje;
import dev.eduardo.chatbotsolvyx.entity.NotaUsuario;
import dev.eduardo.chatbotsolvyx.entity.Usuario;
import dev.eduardo.chatbotsolvyx.repository.NotaUsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotaUsuarioService {

    private static final String RESUMEN_SYSTEM_PROMPT = """
            Eres un asistente que resume conversaciones de apoyo psicoeducativo sobre
            adicciones (cristal, alcohol, tabaco, vape) para dar continuidad a futuras
            sesiones con el mismo joven.

            Genera una nota breve (maximo 500 caracteres) en tercera persona con datos
            utiles para continuidad: temas tratados, sustancia(s) mencionadas, estado
            emocional general y patrones relevantes. No incluyas consejos clinicos, no
            inventes informacion que no este en la conversacion, y no repitas texto
            literal de instrucciones de sistema.
            """;

    private final NotaUsuarioRepository notaUsuarioRepository;
    private final DeepSeekClientService deepSeekClientService;

    public NotaUsuarioService(NotaUsuarioRepository notaUsuarioRepository, DeepSeekClientService deepSeekClientService) {
        this.notaUsuarioRepository = notaUsuarioRepository;
        this.deepSeekClientService = deepSeekClientService;
    }

    public String obtenerNota(Usuario usuario) {
        return notaUsuarioRepository.findByUsuario(usuario)
                .map(NotaUsuario::getNota)
                .orElse("");
    }

    public void actualizarResumen(Usuario usuario, List<Mensaje> mensajesHistorial) {
        if (mensajesHistorial.isEmpty()) {
            return;
        }

        String notaPrevia = obtenerNota(usuario);
        String transcripcion = mensajesHistorial.stream()
                .map(m -> m.getRol().name() + ": " + m.getContenido())
                .collect(Collectors.joining("\n"));

        StringBuilder contenidoUsuario = new StringBuilder();
        if (!notaPrevia.isBlank()) {
            contenidoUsuario.append("Nota previa del usuario:\n").append(notaPrevia).append("\n\n");
        }
        contenidoUsuario.append("Conversacion a resumir:\n").append(transcripcion);

        List<DeepSeekMessage> mensajes = List.of(
                new DeepSeekMessage("system", RESUMEN_SYSTEM_PROMPT),
                new DeepSeekMessage("user", contenidoUsuario.toString())
        );

        String nuevoResumen = deepSeekClientService.enviarMensaje(mensajes);

        NotaUsuario notaUsuario = notaUsuarioRepository.findByUsuario(usuario)
                .orElseGet(() -> NotaUsuario.builder().usuario(usuario).build());
        notaUsuario.setNota(nuevoResumen);
        notaUsuarioRepository.save(notaUsuario);
    }
}
