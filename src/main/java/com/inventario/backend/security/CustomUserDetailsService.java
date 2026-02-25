package com.inventario.backend.security;

import com.inventario.backend.model.User;
import com.inventario.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

    if (!Boolean.TRUE.equals(user.getIsActive())) {
      throw new UsernameNotFoundException("Cuenta desactivada: " + email);
    }

    // .roles() agrega automáticamente el prefijo ROLE_ → ROLE_ADMIN, ROLE_WAREHOUSE_MANAGER,
    // ROLE_VIEWER
    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPasswordHash())
        .roles(user.getRole().name())
        .build();
  }
}
