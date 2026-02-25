package com.inventario.backend.dto.request;

import com.inventario.backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

  @NotBlank(message = "El nombre es requerido")
  private String name;

  @NotBlank(message = "El email es requerido")
  @Email(message = "El email no tiene un formato válido")
  private String email;

  @NotBlank(message = "La contraseña es requerida")
  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
  private String password;

  @NotNull(message = "El rol es requerido")
  private Role role;
}
