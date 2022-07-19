package com.example.azwarakbar.blog.util;

import com.example.azwarakbar.blog.model.Post;
import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {

    private String message;
    private Exception error;
}
