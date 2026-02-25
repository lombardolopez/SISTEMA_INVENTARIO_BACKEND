package com.inventario.backend.service.impl;

import com.inventario.backend.dto.response.DashboardStatsResponse;
import com.inventario.backend.model.MovementType;
import com.inventario.backend.repository.CategoryRepository;
import com.inventario.backend.repository.MovementRepository;
import com.inventario.backend.repository.ProductRepository;
import com.inventario.backend.repository.StockAlertRepository;
import com.inventario.backend.repository.UserRepository;
import com.inventario.backend.service.DashboardService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;
  private final MovementRepository movementRepository;
  private final StockAlertRepository stockAlertRepository;

  @Override
  public DashboardStatsResponse getStats() {
    LocalDateTime startOfDay =
        LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime now = LocalDateTime.now();

    long lowStockCount = productRepository.countAllLowStock();
    long criticalCount = productRepository.countByCurrentStock(0);

    return DashboardStatsResponse.builder()
        .totalProducts(productRepository.count())
        .totalCategories(categoryRepository.count())
        .totalUsers(userRepository.count())
        .lowStockCount(lowStockCount)
        .criticalStockCount(criticalCount)
        .totalMovementsToday(movementRepository.countByCreatedAtBetween(startOfDay, now))
        .totalEntriesToday(
            movementRepository.countByTypeAndCreatedAtBetween(MovementType.ENTRY, startOfDay, now))
        .totalExitsToday(
            movementRepository.countByTypeAndCreatedAtBetween(MovementType.EXIT, startOfDay, now))
        .pendingAlertsCount(stockAlertRepository.countByAcknowledged(false))
        .build();
  }
}
