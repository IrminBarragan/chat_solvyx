package dev.eduardo.chatbotsolvyx.dto.deepseek;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekChatRequest {

    private String model;
    private List<DeepSeekMessage> messages;
    private Double temperature;
}
