package com.example.azwarakbar.blog.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.io.Serializable;

@Data
@JsonPropertyOrder({"success", "data"})
public class ObjectResponse<T> implements Serializable {

    @JsonIgnore
    private static final long serialVersionUID = 7702134516418120340L;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("data")
    private Object data;

    @JsonIgnore
    private HttpStatus status;

    public ObjectResponse() {

    }

    public ObjectResponse(Boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public ObjectResponse(Boolean success, Object data, HttpStatus httpStatus) {
        this.success = success;
        this.data = data;
        this.status = httpStatus;
    }
}
