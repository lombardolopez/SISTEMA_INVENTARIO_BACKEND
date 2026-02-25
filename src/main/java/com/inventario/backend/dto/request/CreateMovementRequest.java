package com.inventario.backend.dto.request;

import com.inventario.backend.model.MovementReason;
import com.inventario.backend.model.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMovementRequest {

  @NotBlank(message = "El producto es requerido")
  private String productId;

  @NotNull(message = "El tipo de movimiento es requerido")
  private MovementType type;

  @Min(value = 1, message = "La cantidad debe ser mayor a 0")
  private int quantity;

  @NotNull(message = "La razón del movimiento es requerida")
  private MovementReason reason;

  // Opcional
  private String notes;
}
