package com.springboot.blog.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;

    @NotEmpty(message = "name should not be empty or null")
    private String name;

    @NotEmpty(message = "email should not be empty or null")
    @Email
    private String email;

    @NotEmpty
    @Size(min = 10, message = "body must be at least 10 characters")
    private String body;
}
