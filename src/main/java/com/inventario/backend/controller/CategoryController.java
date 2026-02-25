package com.inventario.backend.controller;

import com.inventario.backend.dto.request.CreateCategoryRequest;
import com.inventario.backend.dto.request.UpdateCategoryRequest;
import com.inventario.backend.dto.response.ApiResponse;
import com.inventario.backend.dto.response.CategoryResponse;
import com.inventario.backend.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> findAll() {
    return ResponseEntity.ok(ApiResponse.ok(categoryService.findAll()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable String id) {
    return ResponseEntity.ok(ApiResponse.ok(categoryService.findById(id)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<CategoryResponse>> create(
      @Valid @RequestBody CreateCategoryRequest request) {
    CategoryResponse created = categoryService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("Categoría creada exitosamente", created));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryResponse>> update(
      @PathVariable String id, @Valid @RequestBody UpdateCategoryRequest request) {
    return ResponseEntity.ok(
        ApiResponse.ok("Categoría actualizada exitosamente", categoryService.update(id, request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
    categoryService.delete(id);
    return ResponseEntity.ok(ApiResponse.ok("Categoría eliminada exitosamente", null));
  }
}
