package dev.eduardo.chatbotsolvyx.service;

import dev.eduardo.chatbotsolvyx.config.DeepSeekConfig;
import dev.eduardo.chatbotsolvyx.dto.deepseek.DeepSeekMessage;
import dev.eduardo.chatbotsolvyx.dto.request.MensajeRequestDTO;
import dev.eduardo.chatbotsolvyx.dto.response.CerrarSesionResponseDTO;
import dev.eduardo.chatbotsolvyx.dto.response.MensajeResponseDTO;
import dev.eduardo.chatbotsolvyx.entity.Historial;
import dev.eduardo.chatbotsolvyx.entity.Mensaje;
import dev.eduardo.chatbotsolvyx.entity.RolMensaje;
import dev.eduardo.chatbotsolvyx.entity.StatusHistorial;
import dev.eduardo.chatbotsolvyx.entity.Usuario;
import dev.eduardo.chatbotsolvyx.exception.HistorialNoEncontradoException;
import dev.eduardo.chatbotsolvyx.exception.UsuarioNoEncontradoException;
import dev.eduardo.chatbotsolvyx.repository.HistorialRepository;
import dev.eduardo.chatbotsolvyx.repository.MensajeRepository;
import dev.eduardo.chatbotsolvyx.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChatService {

    private final UsuarioRepository usuarioRepository;
    private final HistorialRepository historialRepository;
    private final MensajeRepository mensajeRepository;
    private final ValidacionMensajeService validacionMensajeService;
    private final DeepSeekClientService deepSeekClientService;
    private final NotaUsuarioService notaUsuarioService;

    @Value("${chat.contexto.max-mensajes:10}")
    private int maxMensajesContexto;

    public ChatService(UsuarioRepository usuarioRepository,
                        HistorialRepository historialRepository,
                        MensajeRepository mensajeRepository,
                        ValidacionMensajeService validacionMensajeService,
                        DeepSeekClientService deepSeekClientService,
                        NotaUsuarioService notaUsuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.historialRepository = historialRepository;
        this.mensajeRepository = mensajeRepository;
        this.validacionMensajeService = validacionMensajeService;
        this.deepSeekClientService = deepSeekClientService;
        this.notaUsuarioService = notaUsuarioService;
    }

    public MensajeResponseDTO procesarMensaje(MensajeRequestDTO dto) {
        validacionMensajeService.validarInput(dto.getMensaje());

        Usuario usuario = buscarOCrearUsuario(dto.getUserId(), dto.getNombre());
        Historial historial = buscarOCrearHistorialActivo(usuario);

        Mensaje mensajeUsuario = Mensaje.builder()
                .historial(historial)
                .rol(RolMensaje.USER)
                .contenido(dto.getMensaje())
                .fecha(LocalDateTime.now())
                .build();
        mensajeRepository.save(mensajeUsuario);

        List<DeepSeekMessage> contexto = armarContexto(usuario, historial);
        String respuesta = deepSeekClientService.enviarMensaje(contexto);
        validacionMensajeService.validarOutput(respuesta);

        Mensaje mensajeBot = Mensaje.builder()
                .historial(historial)
                .rol(RolMensaje.BOT)
                .contenido(respuesta)
                .fecha(LocalDateTime.now())
                .build();
        mensajeRepository.save(mensajeBot);

        return MensajeResponseDTO.builder()
                .respuesta(respuesta)
                .historialId(historial.getId())
                .build();
    }

    public CerrarSesionResponseDTO cerrarSesion(String userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No existe un usuario con userId " + userId));

        Historial historial = historialRepository
                .findFirstByUsuarioAndStatusOrderByFechaInicioDesc(usuario, StatusHistorial.ACTIVO)
                .orElseThrow(() -> new HistorialNoEncontradoException("El usuario no tiene una sesion activa"));

        historial.setFechaFin(LocalDateTime.now());
        historial.setStatus(StatusHistorial.CERRADO);
        historialRepository.save(historial);

        List<Mensaje> mensajesHistorial = mensajeRepository.findByHistorialOrderByIdAsc(historial);
        notaUsuarioService.actualizarResumen(usuario, mensajesHistorial);

        return CerrarSesionResponseDTO.builder()
                .historialId(historial.getId())
                .status(historial.getStatus())
                .build();
    }

    private Usuario buscarOCrearUsuario(String userId, String nombre) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseGet(() -> Usuario.builder().id(userId).build());
        usuario.setNombre(nombre);
        return usuarioRepository.save(usuario);
    }

    private Historial buscarOCrearHistorialActivo(Usuario usuario) {
        return historialRepository
                .findFirstByUsuarioAndStatusOrderByFechaInicioDesc(usuario, StatusHistorial.ACTIVO)
                .orElseGet(() -> historialRepository.save(Historial.builder()
                        .usuario(usuario)
                        .fechaInicio(LocalDateTime.now())
                        .status(StatusHistorial.ACTIVO)
                        .build()));
    }

    private List<DeepSeekMessage> armarContexto(Usuario usuario, Historial historial) {
        List<DeepSeekMessage> contexto = new ArrayList<>();
        contexto.add(new DeepSeekMessage("system", DeepSeekConfig.SYSTEM_PROMPT));

        String nota = notaUsuarioService.obtenerNota(usuario);
        if (!nota.isBlank()) {
            contexto.add(new DeepSeekMessage("system", "Notas previas sobre este usuario:\n" + nota));
        }

        List<Mensaje> ultimosMensajes = mensajeRepository.findByHistorialOrderByIdDesc(
                historial, PageRequest.of(0, maxMensajesContexto));
        Collections.reverse(ultimosMensajes);

        for (Mensaje mensaje : ultimosMensajes) {
            String rol = mensaje.getRol() == RolMensaje.USER ? "user" : "assistant";
            contexto.add(new DeepSeekMessage(rol, mensaje.getContenido()));
        }

        return contexto;
    }
}
