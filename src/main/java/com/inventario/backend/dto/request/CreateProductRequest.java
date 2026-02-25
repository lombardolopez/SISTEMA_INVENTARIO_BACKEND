package com.inventario.backend.dto.request;

import com.inventario.backend.model.Unit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProductRequest {

  @NotBlank(message = "El nombre es requerido")
  private String name;

  @NotBlank(message = "La descripción es requerida")
  private String description;

  @NotBlank(message = "La categoría es requerida")
  private String categoryId;

  @NotBlank(message = "El SKU es requerido")
  private String sku;

  @NotNull(message = "La unidad es requerida")
  private Unit unit;

  @Min(value = 0, message = "El stock inicial debe ser mayor o igual a 0")
  private int currentStock;

  @Min(value = 0, message = "El stock mínimo debe ser mayor o igual a 0")
  private int minimumStock;

  @Min(value = 0, message = "El precio unitario debe ser mayor o igual a 0")
  private double unitPrice;

  @NotBlank(message = "La ubicación es requerida")
  private String location;

  // Opcional
  private String imageUrl;
}
