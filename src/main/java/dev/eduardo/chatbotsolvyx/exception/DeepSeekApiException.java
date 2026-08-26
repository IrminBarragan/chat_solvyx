package dev.eduardo.chatbotsolvyx.exception;

public class DeepSeekApiException extends RuntimeException {

    public DeepSeekApiException(String message) {
        super(message);
    }

    public DeepSeekApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
