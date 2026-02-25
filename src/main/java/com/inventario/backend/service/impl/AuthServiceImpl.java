package com.inventario.backend.service.impl;

import com.inventario.backend.dto.request.LoginRequest;
import com.inventario.backend.dto.response.AuthResponse;
import com.inventario.backend.dto.response.UserResponse;
import com.inventario.backend.exception.BusinessException;
import com.inventario.backend.exception.ResourceNotFoundException;
import com.inventario.backend.model.User;
import com.inventario.backend.repository.UserRepository;
import com.inventario.backend.security.CustomUserDetailsService;
import com.inventario.backend.security.JwtTokenProvider;
import com.inventario.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService userDetailsService;

  @Override
  public AuthResponse login(LoginRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

    if (!Boolean.TRUE.equals(user.getIsActive())) {
      throw new BusinessException("Cuenta desactivada. Contacta al administrador.");
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new BusinessException("Credenciales inválidas");
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String token = jwtTokenProvider.generateToken(userDetails);

    return AuthResponse.builder()
        .token(token)
        .expiresIn(jwtTokenProvider.getExpiration())
        .user(UserResponse.from(user))
        .build();
  }

  @Override
  public UserResponse getCurrentUser(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", email));
    return UserResponse.from(user);
  }
}
