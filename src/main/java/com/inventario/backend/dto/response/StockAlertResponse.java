package com.inventario.backend.dto.response;

import com.inventario.backend.model.AlertSeverity;
import com.inventario.backend.model.StockAlert;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertResponse {

  private String id;
  private String productId;
  private String productName;
  private int currentStock;
  private int minimumStock;
  private AlertSeverity severity;
  private boolean acknowledged;
  private String acknowledgedBy;
  private LocalDateTime acknowledgedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static StockAlertResponse from(StockAlert alert) {
    return StockAlertResponse.builder()
        .id(alert.getId())
        .productId(alert.getProductId())
        .productName(alert.getProductName())
        .currentStock(alert.getCurrentStock())
        .minimumStock(alert.getMinimumStock())
        .severity(alert.getSeverity())
        .acknowledged(alert.isAcknowledged())
        .acknowledgedBy(alert.getAcknowledgedBy())
        .acknowledgedAt(alert.getAcknowledgedAt())
        .createdAt(alert.getCreatedAt())
        .updatedAt(alert.getUpdatedAt())
        .build();
  }
}
