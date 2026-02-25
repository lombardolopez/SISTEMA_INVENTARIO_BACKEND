package com.inventario.backend.dto.response;

import com.inventario.backend.model.Movement;
import com.inventario.backend.model.MovementReason;
import com.inventario.backend.model.MovementType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementResponse {

  private String id;
  private String productId;
  private String productName;
  private MovementType type;
  private int quantity;
  private MovementReason reason;
  private String notes;
  private String performedBy;
  private String performedByName;
  private LocalDateTime createdAt;

  public static MovementResponse from(Movement movement) {
    return MovementResponse.builder()
        .id(movement.getId())
        .productId(movement.getProductId())
        .productName(movement.getProductName())
        .type(movement.getType())
        .quantity(movement.getQuantity())
        .reason(movement.getReason())
        .notes(movement.getNotes())
        .performedBy(movement.getPerformedBy())
        .performedByName(movement.getPerformedByName())
        .createdAt(movement.getCreatedAt())
        .build();
  }
}
