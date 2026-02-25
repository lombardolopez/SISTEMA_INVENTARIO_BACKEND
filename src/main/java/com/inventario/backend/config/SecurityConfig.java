package com.inventario.backend.config;

import com.inventario.backend.security.CustomUserDetailsService;
import com.inventario.backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final CustomUserDetailsService userDetailsService;

  @Value("${app.cors.allowed-origins}")
  private String allowedOrigins;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Manejo de errores de autenticación/autorización → JSON limpio
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                          response.setContentType("application/json;charset=UTF-8");
                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response
                              .getWriter()
                              .write(
                                  "{\"success\":false,\"message\":\"No autenticado. Proporciona un"
                                      + " token válido.\",\"data\":null}");
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          response.setContentType("application/json;charset=UTF-8");
                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response
                              .getWriter()
                              .write(
                                  "{\"success\":false,\"message\":\"No tienes permisos para"
                                      + " realizar esta acción.\",\"data\":null}");
                        }))
        .authorizeHttpRequests(
            auth ->
                auth

                    // ── Rutas públicas ──────────────────────────────────────────
                    .requestMatchers(HttpMethod.POST, "/api/auth/login")
                    .permitAll()

                    // ── Solo ADMIN: gestión de usuarios ─────────────────────────
                    .requestMatchers("/api/users/**")
                    .hasRole("ADMIN")

                    // ── Solo ADMIN: escritura de categorías ─────────────────────
                    .requestMatchers(HttpMethod.POST, "/api/categories")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/categories/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/categories/**")
                    .hasRole("ADMIN")

                    // ── Solo ADMIN: eliminar productos y forzar generación de alertas
                    .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/alerts/generate")
                    .hasRole("ADMIN")

                    // ── ADMIN + WAREHOUSE_MANAGER: crear/editar productos ───────
                    .requestMatchers(HttpMethod.POST, "/api/products")
                    .hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                    .requestMatchers(HttpMethod.PUT, "/api/products/**")
                    .hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")

                    // ── ADMIN + WAREHOUSE_MANAGER: registrar movimientos ─────────
                    .requestMatchers(HttpMethod.POST, "/api/movements")
                    .hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")

                    // ── ADMIN + WAREHOUSE_MANAGER: acknowledge alertas ───────────
                    .requestMatchers(HttpMethod.PATCH, "/api/alerts/**")
                    .hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")

                    // ── Cualquier usuario autenticado: lectura general ───────────
                    .anyRequest()
                    .authenticated())
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(allowedOrigins));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }
}
