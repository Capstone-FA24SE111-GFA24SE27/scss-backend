package com.capstone2024.gym_management_system.domain.account.services;


import com.capstone2024.gym_management_system.application.advice.exeptions.NotFoundException;
import com.capstone2024.gym_management_system.application.authentication.dto.request.LoginRequestDTO;
import com.capstone2024.gym_management_system.application.authentication.dto.JwtTokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface AuthenticationService {
    public JwtTokenDTO login(LoginRequestDTO loginRequestDTO, HttpServletResponse response) throws NotFoundException;

    JwtTokenDTO refreshAccessToken(HttpServletResponse response, HttpServletRequest request);
}
