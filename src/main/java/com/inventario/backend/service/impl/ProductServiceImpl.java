package com.inventario.backend.service.impl;

import com.inventario.backend.dto.request.CreateProductRequest;
import com.inventario.backend.dto.request.UpdateProductRequest;
import com.inventario.backend.dto.response.PagedResponse;
import com.inventario.backend.dto.response.ProductResponse;
import com.inventario.backend.exception.BusinessException;
import com.inventario.backend.exception.ResourceNotFoundException;
import com.inventario.backend.model.Category;
import com.inventario.backend.model.Product;
import com.inventario.backend.repository.CategoryRepository;
import com.inventario.backend.repository.ProductRepository;
import com.inventario.backend.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public PagedResponse<ProductResponse> findAll(
      String search, String categoryId, Pageable pageable) {
    boolean hasSearch = search != null && !search.isBlank();
    boolean hasCategory = categoryId != null && !categoryId.isBlank();

    Page<Product> page;

    if (hasSearch && hasCategory) {
      page =
          productRepository.findByCategoryIdAndNameContainingIgnoreCase(
              categoryId, search, pageable);
    } else if (hasSearch) {
      page = productRepository.findByNameContainingIgnoreCase(search, pageable);
    } else if (hasCategory) {
      page = productRepository.findByCategoryId(categoryId, pageable);
    } else {
      page = productRepository.findAll(pageable);
    }

    return PagedResponse.of(page.map(this::toResponse));
  }

  @Override
  public ProductResponse findById(String id) {
    return toResponse(findProductById(id));
  }

  @Override
  public List<ProductResponse> findLowStock() {
    return productRepository.findAllLowStock().stream().map(this::toResponse).toList();
  }

  @Override
  public ProductResponse create(CreateProductRequest request) {
    if (productRepository.existsBySku(request.getSku())) {
      throw new BusinessException(409, "Ya existe un producto con ese SKU: " + request.getSku());
    }
    validateCategoryExists(request.getCategoryId());

    Product product =
        Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .categoryId(request.getCategoryId())
            .sku(request.getSku())
            .unit(request.getUnit())
            .currentStock(request.getCurrentStock())
            .minimumStock(request.getMinimumStock())
            .unitPrice(request.getUnitPrice())
            .location(request.getLocation())
            .imageUrl(request.getImageUrl())
            .build();

    return toResponse(productRepository.save(product));
  }

  @Override
  public ProductResponse update(String id, UpdateProductRequest request) {
    Product product = findProductById(id);

    if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
      throw new BusinessException(409, "Ya existe un producto con ese SKU: " + request.getSku());
    }
    validateCategoryExists(request.getCategoryId());

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setCategoryId(request.getCategoryId());
    product.setSku(request.getSku());
    product.setUnit(request.getUnit());
    product.setCurrentStock(request.getCurrentStock());
    product.setMinimumStock(request.getMinimumStock());
    product.setUnitPrice(request.getUnitPrice());
    product.setLocation(request.getLocation());
    product.setImageUrl(request.getImageUrl());

    return toResponse(productRepository.save(product));
  }

  @Override
  public void delete(String id) {
    findProductById(id);
    productRepository.deleteById(id);
  }

  // ── helpers ─────────────────────────────────────────────────────────────

  private Product findProductById(String id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
  }

  private void validateCategoryExists(String categoryId) {
    if (!categoryRepository.existsById(categoryId)) {
      throw new ResourceNotFoundException("Categoría", categoryId);
    }
  }

  private ProductResponse toResponse(Product product) {
    String categoryName =
        categoryRepository
            .findById(product.getCategoryId())
            .map(Category::getName)
            .orElse("Sin categoría");
    return ProductResponse.from(product, categoryName);
  }
}
