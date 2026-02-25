package com.inventario.backend.dto.response;

import com.inventario.backend.model.Category;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

  private String id;
  private String name;
  private String description;
  private String color;
  // Calculado en servicio, no persistido en MongoDB
  private long productCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static CategoryResponse from(Category category, long productCount) {
    return CategoryResponse.builder()
        .id(category.getId())
        .name(category.getName())
        .description(category.getDescription())
        .color(category.getColor())
        .productCount(productCount)
        .createdAt(category.getCreatedAt())
        .updatedAt(category.getUpdatedAt())
        .build();
  }
}
