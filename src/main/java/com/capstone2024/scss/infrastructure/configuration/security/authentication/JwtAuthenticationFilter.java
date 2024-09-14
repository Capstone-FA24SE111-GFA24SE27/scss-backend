package com.capstone2024.scss.infrastructure.configuration.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public final class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String email = checkIfAlreadyAuthenticatedAndExtractEmailFromJwtToken(request);

        if (StringUtils.isNotBlank(email)) {
            // Token hợp lệ, tiếp tục xử lý
            logger.info("Token validated successfully for email: {}", email);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("User {} authenticated and set in SecurityContext", email);
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isBlank(bearerToken) || !StringUtils.startsWith(bearerToken, BEARER_PREFIX)) {
            logger.debug("No Bearer token found in the request");
            return null;
        }
        return bearerToken.substring(BEARER_PREFIX.length());
    }

    private String checkIfAlreadyAuthenticatedAndExtractEmailFromJwtToken(HttpServletRequest request) {
        if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
            logger.debug("User already authenticated");
            return null;
        }
        String token = extractJwtTokenFromRequest(request);
        if (Objects.isNull(token)) {
            logger.debug("No token extracted from request");
            return null;
        }
        String email = jwtService.validateJwtToken(token);
        if (email == null) {
            logger.warn("Invalid token provided");
        }
        return email;
    }
}
