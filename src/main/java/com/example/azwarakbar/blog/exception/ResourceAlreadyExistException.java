package com.example.azwarakbar.blog.exception;

import com.example.azwarakbar.blog.schema.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceAlreadyExistException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private transient MessageResponse messageResponse;

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ResourceAlreadyExistException(String resourceName, String fieldName, Object fieldValue) {
        super();
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.setMessageResponse();
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public MessageResponse getMessageResponse() {
        return messageResponse;
    }

    private void setMessageResponse() {
        String message = String.format("%s is already exist with %s: '%s'", resourceName, fieldName, fieldValue);

        messageResponse = new MessageResponse(Boolean.FALSE, message);
    }
}