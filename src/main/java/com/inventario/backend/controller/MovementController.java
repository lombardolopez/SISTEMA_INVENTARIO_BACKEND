package com.inventario.backend.controller;

import com.inventario.backend.dto.request.CreateMovementRequest;
import com.inventario.backend.dto.response.ApiResponse;
import com.inventario.backend.dto.response.MovementResponse;
import com.inventario.backend.dto.response.PagedResponse;
import com.inventario.backend.model.MovementType;
import com.inventario.backend.service.MovementService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

  private final MovementService movementService;

  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<MovementResponse>>> findAll(
      @RequestParam(required = false) MovementType type,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime to,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.ok(movementService.findAll(type, from, to, pageable)));
  }

  // product/{productId} ANTES de /{id} — "product" es literal, tiene prioridad
  @GetMapping("/product/{productId}")
  public ResponseEntity<ApiResponse<PagedResponse<MovementResponse>>> findByProduct(
      @PathVariable String productId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.ok(movementService.findByProduct(productId, pageable)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<MovementResponse>> findById(@PathVariable String id) {
    return ResponseEntity.ok(ApiResponse.ok(movementService.findById(id)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<MovementResponse>> create(
      @Valid @RequestBody CreateMovementRequest request, Authentication authentication) {
    MovementResponse created = movementService.create(request, authentication.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok("Movimiento registrado exitosamente", created));
  }
}
