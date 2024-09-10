package com.capstone2024.gym_management_system.infrastructure.configuration.security;

import com.capstone2024.gym_management_system.domain.account.enums.Role;
import com.capstone2024.gym_management_system.infrastructure.configuration.oauth.google.CustomOAuth2AuthenticationSuccessHandler;
import com.capstone2024.gym_management_system.infrastructure.configuration.oauth.google.CustomOAuth2UserService;
import com.capstone2024.gym_management_system.infrastructure.configuration.security.authentication.CustomAccessDeniedHandler;
import com.capstone2024.gym_management_system.infrastructure.configuration.security.authentication.JwtAuthenticationEntryPoint;
import com.capstone2024.gym_management_system.infrastructure.configuration.security.authentication.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService, CustomAccessDeniedHandler customAccessDeniedHandler, CustomOAuth2UserService customOAuth2UserService, CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOAuth2AuthenticationSuccessHandler = customOAuth2AuthenticationSuccessHandler;
    }

    @Bean
    @SuppressWarnings("java:S1192")
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
//                .requiresChannel(config ->
//                        config.anyRequest()
//                                .requiresSecure())

                .authorizeHttpRequests(config ->
                        config.requestMatchers("/api/auth/**")
                                .permitAll())

                .authorizeHttpRequests(config ->
                        config.requestMatchers("/api/information/**")
                                .permitAll())

                .authorizeHttpRequests(config ->
                        config.requestMatchers("/ws/**")
                                .permitAll())

//                .authorizeHttpRequests(config ->
//                        config.requestMatchers(HttpMethod.GET,"/api/profile/**")
//                                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.STUDENT.name()))

                .authorizeHttpRequests(config ->
                        config.requestMatchers(HttpMethod.GET,"/api/account/**")
                                .hasAnyRole(Role.ADMINISTRATOR.name()))

                .authorizeHttpRequests(config ->
                        config.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api-docs/**")
                                .permitAll())

                .authorizeHttpRequests(config ->
                        config.anyRequest()
                                .authenticated())
//                .authorizeHttpRequests(config ->
//                        config.anyRequest()
//                                .permitAll())
                .exceptionHandling(config -> {
                    config.authenticationEntryPoint(authenticationEntryPoint);
                    config.accessDeniedHandler(customAccessDeniedHandler);
                })
                .sessionManagement(config ->
                        config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint()
                        .userService(customOAuth2UserService)
                        .and()
                        .successHandler(customOAuth2AuthenticationSuccessHandler)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A);
    }

    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000"));
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Replace with your frontend URL
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
