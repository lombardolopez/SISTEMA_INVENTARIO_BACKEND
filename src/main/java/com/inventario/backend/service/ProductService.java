package com.inventario.backend.service;

import com.inventario.backend.dto.request.CreateProductRequest;
import com.inventario.backend.dto.request.UpdateProductRequest;
import com.inventario.backend.dto.response.PagedResponse;
import com.inventario.backend.dto.response.ProductResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  PagedResponse<ProductResponse> findAll(String search, String categoryId, Pageable pageable);

  ProductResponse findById(String id);

  List<ProductResponse> findLowStock();

  ProductResponse create(CreateProductRequest request);

  ProductResponse update(String id, UpdateProductRequest request);

  void delete(String id);
}
