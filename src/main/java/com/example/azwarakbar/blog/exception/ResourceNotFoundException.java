package com.example.azwarakbar.blog.exception;

import com.example.azwarakbar.blog.schema.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private transient MessageResponse messageResponse;

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super();
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.setApiResponse();
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

    private void setApiResponse() {
        String message = String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue);

        messageResponse = new MessageResponse(Boolean.FALSE, message);
    }
}
