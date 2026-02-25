package com.inventario.backend.repository;

import com.inventario.backend.model.AlertSeverity;
import com.inventario.backend.model.StockAlert;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAlertRepository extends MongoRepository<StockAlert, String> {

  Optional<StockAlert> findByProductId(String productId);

  List<StockAlert> findByAcknowledged(boolean acknowledged);

  List<StockAlert> findBySeverity(AlertSeverity severity);

  List<StockAlert> findByAcknowledgedAndSeverity(boolean acknowledged, AlertSeverity severity);

  void deleteByProductId(String productId);

  long countByAcknowledged(boolean acknowledged);

  long countBySeverity(AlertSeverity severity);
}
