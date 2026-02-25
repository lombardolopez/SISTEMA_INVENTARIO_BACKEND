package com.inventario.backend.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movements")
@CompoundIndex(name = "product_date_idx", def = "{'productId': 1, 'createdAt': -1}")
public class Movement {

  @Id private String id;

  private String productId;

  // Denormalizado para historial legible sin JOIN
  private String productName;

  @Indexed private MovementType type;

  private int quantity;

  private MovementReason reason;

  private String notes;

  private String performedBy;

  // Denormalizado para historial legible sin JOIN
  private String performedByName;

  @CreatedDate private LocalDateTime createdAt;

  // SIN updatedAt — los movimientos son inmutables
}
