package dev.eduardo.chatbotsolvyx.dto.deepseek;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekMessage {

    private String role;
    private String content;
}
