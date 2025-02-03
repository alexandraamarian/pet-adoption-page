package org.ubb.adoption_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ubb.adoption_service.api.AuthResponse;
import org.ubb.adoption_service.api.LoginRequest;
import org.ubb.adoption_service.api.RegisterRequest;
import org.ubb.adoption_service.model.UserEntity;
import org.ubb.adoption_service.service.security.JwtService;
import org.ubb.adoption_service.service.security.UserService;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController
{
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService)
    {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request)
    {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        userService.authenticateUser(request);
        UserEntity userEntity = userService.getUserByUsername(request.userName());
        String token = jwtService.generateToken(userEntity);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
