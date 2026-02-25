package com.inventario.backend.service;

import com.inventario.backend.dto.response.DashboardStatsResponse;

public interface DashboardService {

  DashboardStatsResponse getStats();
}
