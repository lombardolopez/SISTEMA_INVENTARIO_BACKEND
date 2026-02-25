package com.inventario.backend.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stock_alerts")
public class StockAlert {

  @Id private String id;

  // Índice único: una sola alerta activa por producto
  @Indexed(unique = true)
  private String productId;

  // Denormalizado para consultas rápidas
  private String productName;

  private int currentStock;

  private int minimumStock;

  @Indexed private AlertSeverity severity;

  @Indexed private boolean acknowledged;

  // Id del usuario que confirmó la alerta (nullable)
  private String acknowledgedBy;

  private LocalDateTime acknowledgedAt;

  @CreatedDate private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime updatedAt;
}
