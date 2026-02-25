package com.inventario.backend.service;

import com.inventario.backend.dto.request.CreateMovementRequest;
import com.inventario.backend.dto.response.MovementResponse;
import com.inventario.backend.dto.response.PagedResponse;
import com.inventario.backend.model.MovementType;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

public interface MovementService {

  PagedResponse<MovementResponse> findAll(
      MovementType type, LocalDateTime from, LocalDateTime to, Pageable pageable);

  MovementResponse findById(String id);

  PagedResponse<MovementResponse> findByProduct(String productId, Pageable pageable);

  MovementResponse create(CreateMovementRequest request, String performedByEmail);
}
