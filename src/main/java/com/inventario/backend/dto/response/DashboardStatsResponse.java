package com.inventario.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

  private long totalProducts;
  private long totalCategories;
  private long totalUsers;
  private long lowStockCount;
  private long criticalStockCount;
  private long totalMovementsToday;
  private long totalEntriesToday;
  private long totalExitsToday;
  private long pendingAlertsCount;
}
