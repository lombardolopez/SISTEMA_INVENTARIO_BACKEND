package com.inventario.backend.repository;

import com.inventario.backend.model.Movement;
import com.inventario.backend.model.MovementType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementRepository extends MongoRepository<Movement, String> {

  Page<Movement> findByProductId(String productId, Pageable pageable);

  Page<Movement> findByType(MovementType type, Pageable pageable);

  Page<Movement> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

  Page<Movement> findByTypeAndCreatedAtBetween(
      MovementType type, LocalDateTime from, LocalDateTime to, Pageable pageable);

  // Para el dashboard: movimientos del día
  List<Movement> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

  long countByTypeAndCreatedAtBetween(MovementType type, LocalDateTime from, LocalDateTime to);

  long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
