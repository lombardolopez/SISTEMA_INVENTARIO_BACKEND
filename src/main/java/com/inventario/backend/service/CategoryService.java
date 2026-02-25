package com.inventario.backend.service;

import com.inventario.backend.dto.request.CreateCategoryRequest;
import com.inventario.backend.dto.request.UpdateCategoryRequest;
import com.inventario.backend.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

  List<CategoryResponse> findAll();

  CategoryResponse findById(String id);

  CategoryResponse create(CreateCategoryRequest request);

  CategoryResponse update(String id, UpdateCategoryRequest request);

  void delete(String id);
}
