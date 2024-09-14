package com.capstone2024.scss.infrastructure.configuration.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public final class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        System.out.println("AuthenticationEntryPoint/////////////////////////////////");
        System.out.println(authException.toString());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: No Authorization header");
    }
}
