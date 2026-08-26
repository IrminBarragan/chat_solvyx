package dev.eduardo.chatbotsolvyx.controller;

import dev.eduardo.chatbotsolvyx.dto.request.CerrarSesionRequestDTO;
import dev.eduardo.chatbotsolvyx.dto.request.MensajeRequestDTO;
import dev.eduardo.chatbotsolvyx.dto.response.CerrarSesionResponseDTO;
import dev.eduardo.chatbotsolvyx.dto.response.MensajeResponseDTO;
import dev.eduardo.chatbotsolvyx.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/mensaje")
    public ResponseEntity<MensajeResponseDTO> enviarMensaje(@Valid @RequestBody MensajeRequestDTO dto) {
        return ResponseEntity.ok(chatService.procesarMensaje(dto));
    }

    @PostMapping("/cerrar-sesion")
    public ResponseEntity<CerrarSesionResponseDTO> cerrarSesion(@Valid @RequestBody CerrarSesionRequestDTO dto) {
        return ResponseEntity.ok(chatService.cerrarSesion(dto.getUserId()));
    }
}
