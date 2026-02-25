package com.inventario.backend.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
@CompoundIndex(name = "stock_idx", def = "{'currentStock': 1, 'minimumStock': 1}")
public class Product {

  @Id private String id;

  @TextIndexed private String name;

  @TextIndexed private String description;

  @Field("categoryId")
  private String categoryId;

  @Indexed(unique = true)
  private String sku;

  private Unit unit;

  private int currentStock;

  private int minimumStock;

  private double unitPrice;

  private String location;

  private String imageUrl;

  @CreatedDate private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime updatedAt;
}
