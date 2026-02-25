package com.inventario.backend.service;

import com.inventario.backend.dto.request.LoginRequest;
import com.inventario.backend.dto.response.AuthResponse;
import com.inventario.backend.dto.response.UserResponse;

public interface AuthService {

  AuthResponse login(LoginRequest request);

  UserResponse getCurrentUser(String email);
}
