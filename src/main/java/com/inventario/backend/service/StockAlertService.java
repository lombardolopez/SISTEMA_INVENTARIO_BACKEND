package com.inventario.backend.service;

import com.inventario.backend.dto.response.StockAlertResponse;
import com.inventario.backend.model.AlertSeverity;
import java.util.List;

public interface StockAlertService {

  List<StockAlertResponse> findAll(Boolean acknowledged, AlertSeverity severity);

  StockAlertResponse acknowledge(String alertId, String userId);

  void generateAlerts();

  // Llamado por MovementService tras cada movimiento de stock
  void updateAlertForProduct(
      String productId, String productName, int currentStock, int minimumStock);
}
