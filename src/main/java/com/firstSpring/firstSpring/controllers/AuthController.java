package com.firstSpring.firstSpring.controllers;

import com.firstSpring.firstSpring.dto.UserLogin;
import com.firstSpring.firstSpring.dto.UserRegister;
import com.firstSpring.firstSpring.service.AuthService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *  Controller to manage different auth endpoints necessaries, all endpoints return an object TokenResponse with the
 *  access token and refresh token, just that.
 *  Endpoint 'POST /auth/log-in' to manage the user log in returning the mentioned before.
 *  Endpoint 'POST /auth/sign-up' to register a new user, returning the mentioned before.
 *  Endpoint 'POST /auth/refresh' to get a new access token and refresh token (in this project I don't use that)
 *
 * @author Daniel Mora Cantillo
 */
@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/log-in")
    public ResponseEntity<?> login(@RequestBody final UserLogin userDTO) {
        return authService.login(userDTO);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody final UserRegister userDTO) {
        return authService.register(userDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        return authService.refreshToken(authHeader);
    }
}
