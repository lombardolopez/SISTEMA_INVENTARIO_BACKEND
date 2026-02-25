package com.inventario.backend.dto.response;

import com.inventario.backend.model.Product;
import com.inventario.backend.model.Unit;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

  private String id;
  private String name;
  private String description;
  private String categoryId;
  // Denormalizado para la UI — resuelto en el servicio
  private String categoryName;
  private String sku;
  private Unit unit;
  private int currentStock;
  private int minimumStock;
  private double unitPrice;
  private String location;
  private String imageUrl;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static ProductResponse from(Product product, String categoryName) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .categoryId(product.getCategoryId())
        .categoryName(categoryName)
        .sku(product.getSku())
        .unit(product.getUnit())
        .currentStock(product.getCurrentStock())
        .minimumStock(product.getMinimumStock())
        .unitPrice(product.getUnitPrice())
        .location(product.getLocation())
        .imageUrl(product.getImageUrl())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .build();
  }
}
