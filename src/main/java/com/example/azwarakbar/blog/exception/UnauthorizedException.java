package com.example.azwarakbar.blog.exception;

import com.example.azwarakbar.blog.schema.MessageResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
@JsonIgnoreProperties({"cause", "stackTrace"})
public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private MessageResponse messageResponse;

    private String message;

    public UnauthorizedException(MessageResponse messageResponse) {
        super();
        this.messageResponse = messageResponse;
    }

    public UnauthorizedException(String message) {
        super(message);
        this.message = message;
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageResponse getMessageResponse() {
        return messageResponse;
    }

    public void setMessageResponse(MessageResponse messageResponse) {
        this.messageResponse = messageResponse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
