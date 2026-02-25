package com.inventario.backend.controller;

import com.inventario.backend.dto.request.CreateProductRequest;
import com.inventario.backend.dto.request.UpdateProductRequest;
import com.inventario.backend.dto.response.ApiResponse;
import com.inventario.backend.dto.response.PagedResponse;
import com.inventario.backend.dto.response.ProductResponse;
import com.inventario.backend.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<ProductResponse>>> findAll(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String categoryId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.ok(productService.findAll(search, categoryId, pageable)));
  }

  // low-stock ANTES de /{id} para que Spring no lo confunda con un ID literal
  @GetMapping("/low-stock")
  public ResponseEntity<ApiResponse<List<ProductResponse>>> findLowStock() {
    return ResponseEntity.ok(ApiResponse.ok(productService.findLowStock()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable String id) {
    return ResponseEntity.ok(ApiResponse.ok(productService.findById(id)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ProductResponse>> create(
      @Valid @RequestBody CreateProductRequest request) {
    ProductResponse created = productService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("Producto creado exitosamente", created));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ProductResponse>> update(
      @PathVariable String id, @Valid @RequestBody UpdateProductRequest request) {
    return ResponseEntity.ok(
        ApiResponse.ok("Producto actualizado exitosamente", productService.update(id, request)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
    productService.delete(id);
    return ResponseEntity.ok(ApiResponse.ok("Producto eliminado exitosamente", null));
  }
}
