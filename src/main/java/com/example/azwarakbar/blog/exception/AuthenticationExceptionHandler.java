package com.example.azwarakbar.blog.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public class AuthenticationExceptionHandler  implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        UnauthorizedException exception = new UnauthorizedException("Please provide valid authentication token");
        String responseMsg = mapper.writeValueAsString(exception);
        response.getWriter().write(responseMsg);
    }
}
