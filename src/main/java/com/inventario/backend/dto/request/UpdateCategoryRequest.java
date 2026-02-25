package com.inventario.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateCategoryRequest {

  @NotBlank(message = "El nombre es requerido")
  private String name;

  @NotBlank(message = "La descripción es requerida")
  private String description;

  @NotBlank(message = "El color es requerido")
  @Pattern(
      regexp = "^#[0-9A-Fa-f]{6}$",
      message = "El color debe ser un código hex válido (ej: #8B5E3C)")
  private String color;
}
