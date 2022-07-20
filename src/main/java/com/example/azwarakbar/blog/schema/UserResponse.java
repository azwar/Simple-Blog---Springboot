package com.example.azwarakbar.blog.schema;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
	private Long id;
	private String username;
	private String name;
	private String email;
}
