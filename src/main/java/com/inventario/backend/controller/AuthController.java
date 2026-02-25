package com.inventario.backend.controller;

import com.inventario.backend.dto.request.LoginRequest;
import com.inventario.backend.dto.response.ApiResponse;
import com.inventario.backend.dto.response.AuthResponse;
import com.inventario.backend.dto.response.UserResponse;
import com.inventario.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(ApiResponse.ok("Login exitoso", response));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
    UserResponse user = authService.getCurrentUser(authentication.getName());
    return ResponseEntity.ok(ApiResponse.ok(user));
  }
}
