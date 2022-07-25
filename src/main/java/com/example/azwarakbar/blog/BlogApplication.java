package com.example.azwarakbar.blog;

import com.example.azwarakbar.blog.exception.AuthenticationExceptionHandler;
import com.example.azwarakbar.blog.secure.JWTAuthenticationFilterUser;
import com.example.azwarakbar.blog.secure.JwtAuthenticationFilter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.convert.Jsr310Converters;

@SpringBootApplication
@EntityScan(basePackageClasses = { BlogApplication.class, Jsr310Converters.class })
public class BlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter();
	}

	@Bean
	public AuthenticationExceptionHandler authenticationExceptionHandler() {
		return new AuthenticationExceptionHandler();
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
