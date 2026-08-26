package dev.eduardo.chatbotsolvyx.service;

import dev.eduardo.chatbotsolvyx.config.DeepSeekConfig;
import dev.eduardo.chatbotsolvyx.dto.deepseek.DeepSeekChatRequest;
import dev.eduardo.chatbotsolvyx.dto.deepseek.DeepSeekChatResponse;
import dev.eduardo.chatbotsolvyx.dto.deepseek.DeepSeekMessage;
import dev.eduardo.chatbotsolvyx.exception.DeepSeekApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class DeepSeekClientService {

    private final RestClient deepSeekRestClient;
    private final DeepSeekConfig deepSeekConfig;

    public DeepSeekClientService(RestClient deepSeekRestClient, DeepSeekConfig deepSeekConfig) {
        this.deepSeekRestClient = deepSeekRestClient;
        this.deepSeekConfig = deepSeekConfig;
    }

    public String enviarMensaje(List<DeepSeekMessage> mensajes) {
        DeepSeekChatRequest request = new DeepSeekChatRequest(deepSeekConfig.getApiModel(), mensajes, 0.7);

        try {
            DeepSeekChatResponse response = deepSeekRestClient.post()
                    .body(request)
                    .retrieve()
                    .body(DeepSeekChatResponse.class);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new DeepSeekApiException("DeepSeek no devolvio una respuesta valida");
            }
            return response.getChoices().get(0).getMessage().getContent();
        } catch (RestClientException ex) {
            throw new DeepSeekApiException("Error al comunicarse con DeepSeek", ex);
        }
    }
}
