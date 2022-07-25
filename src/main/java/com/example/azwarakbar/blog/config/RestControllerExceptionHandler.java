package com.example.azwarakbar.blog.config;

import com.example.azwarakbar.blog.exception.*;
import com.example.azwarakbar.blog.schema.MessageResponse;
import com.example.azwarakbar.blog.schema.ResponseException;
import com.example.azwarakbar.blog.secure.JwtAuthenticationEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class RestControllerExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestControllerExceptionHandler.class);

    public ResponseEntity<MessageResponse> resolveException(BlogException exception) {
        String message = exception.getMessage();
        HttpStatus status = exception.getStatus();

        MessageResponse apiResponse = new MessageResponse();

        apiResponse.setSuccess(Boolean.FALSE);
        apiResponse.setMessage(message);

        return new ResponseEntity<>(apiResponse, status);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<MessageResponse> resolveException(UnauthorizedException exception) {
        LOGGER.error("Ini ada errornya");
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage(exception.getMessage());
        messageResponse.setSuccess(false);
        messageResponse.setStatus(HttpStatus.UNAUTHORIZED);
        exception.setMessageResponse(messageResponse);

        return new ResponseEntity<>(exception.getMessageResponse(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ResponseEntity<MessageResponse> resolveException(BadRequestException exception) {
        MessageResponse messageResponse = exception.getMessageResponse();

        return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<MessageResponse> resolveException(ResourceNotFoundException exception) {
        MessageResponse response = exception.getMessageResponse();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    @ResponseBody
    public ResponseEntity<MessageResponse> resolveException(ResourceAlreadyExistException exception) {
        MessageResponse response = exception.getMessageResponse();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<MessageResponse> resolveException(AccessDeniedException exception) {
        MessageResponse response = exception.getMessageResponse();

        return new ResponseEntity< >(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseException> resolveException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> messages = new ArrayList<>(fieldErrors.size());
        for (FieldError error : fieldErrors) {
            messages.add(error.getField() + " - " + error.getDefaultMessage());
        }
        return new ResponseEntity<>(new ResponseException(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseException> resolveException(MethodArgumentTypeMismatchException ex) {
        String message = "Parameter '" + ex.getParameter().getParameterName() + "' must be '"
                + Objects.requireNonNull(ex.getRequiredType()).getSimpleName() + "'";
        List<String> messages = new ArrayList<>(1);
        messages.add(message);
        return new ResponseEntity<>(new ResponseException(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public ResponseEntity<ResponseException> resolveException(HttpRequestMethodNotSupportedException ex) {
        String message = "Request method '" + ex.getMethod() + "' not supported. List of all supported methods - "
                + ex.getSupportedHttpMethods();
        List<String> messages = new ArrayList<>(1);
        messages.add(message);

        return new ResponseEntity<>(new ResponseException(messages, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                HttpStatus.METHOD_NOT_ALLOWED.value()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseException> resolveException(HttpMessageNotReadableException ex) {
        String message = "Please provide Request Body in valid JSON format";
        List<String> messages = new ArrayList<>(1);
        messages.add(message);
        return new ResponseEntity<>(new ResponseException(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
}
