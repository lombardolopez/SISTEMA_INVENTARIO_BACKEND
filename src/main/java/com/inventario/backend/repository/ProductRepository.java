package com.inventario.backend.repository;

import com.inventario.backend.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

  boolean existsBySku(String sku);

  boolean existsBySkuAndIdNot(String sku, String id);

  Optional<Product> findBySku(String sku);

  Page<Product> findByCategoryId(String categoryId, Pageable pageable);

  long countByCategoryId(String categoryId);

  // Productos con stock > 0 pero <= minimumStock (warning)
  @Query(
      "{ $expr: { $and: [ { $gt: ['$currentStock', 0] }, { $lte: ['$currentStock', '$minimumStock']"
          + " } ] } }")
  List<Product> findWarningStock();

  // Productos con stock == 0 (critical)
  List<Product> findByCurrentStock(int stock);

  long countByCurrentStock(int stock);

  // Todos los productos con currentStock <= minimumStock (incluye críticos)
  @Query("{ $expr: { $lte: ['$currentStock', '$minimumStock'] } }")
  List<Product> findAllLowStock();

  @Query(value = "{ $expr: { $lte: ['$currentStock', '$minimumStock'] } }", count = true)
  long countAllLowStock();

  Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

  Page<Product> findByCategoryIdAndNameContainingIgnoreCase(
      String categoryId, String name, Pageable pageable);
}
