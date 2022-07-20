package com.example.azwarakbar.blog.schema;

import com.example.azwarakbar.blog.model.Category;
import com.example.azwarakbar.blog.model.Comment;
import com.example.azwarakbar.blog.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@Data
public class RequestPost {

    @Length(min = 5, message = "*Your title must have at least 5 characters")
    @NotEmpty(message = "*Please provide title")
    private String title;

    @NotEmpty(message = "*Please provide body")
    private String body;

    @NotNull(message = "*Please provide category id (category.id)")
    private Category category;

    private Boolean status;
}
