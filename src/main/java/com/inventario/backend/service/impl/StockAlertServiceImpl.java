package com.inventario.backend.service.impl;

import com.inventario.backend.dto.response.StockAlertResponse;
import com.inventario.backend.exception.ResourceNotFoundException;
import com.inventario.backend.model.AlertSeverity;
import com.inventario.backend.model.Product;
import com.inventario.backend.model.StockAlert;
import com.inventario.backend.repository.ProductRepository;
import com.inventario.backend.repository.StockAlertRepository;
import com.inventario.backend.service.StockAlertService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockAlertServiceImpl implements StockAlertService {

  private final StockAlertRepository stockAlertRepository;
  private final ProductRepository productRepository;

  @Override
  public List<StockAlertResponse> findAll(Boolean acknowledged, AlertSeverity severity) {
    List<StockAlert> alerts;

    if (acknowledged != null && severity != null) {
      alerts = stockAlertRepository.findByAcknowledgedAndSeverity(acknowledged, severity);
    } else if (acknowledged != null) {
      alerts = stockAlertRepository.findByAcknowledged(acknowledged);
    } else if (severity != null) {
      alerts = stockAlertRepository.findBySeverity(severity);
    } else {
      alerts = stockAlertRepository.findAll();
    }

    return alerts.stream().map(StockAlertResponse::from).toList();
  }

  @Override
  public StockAlertResponse acknowledge(String alertId, String userId) {
    StockAlert alert =
        stockAlertRepository
            .findById(alertId)
            .orElseThrow(() -> new ResourceNotFoundException("Alerta", alertId));

    alert.setAcknowledged(true);
    alert.setAcknowledgedBy(userId);
    alert.setAcknowledgedAt(java.time.LocalDateTime.now());

    return StockAlertResponse.from(stockAlertRepository.save(alert));
  }

  @Override
  public void generateAlerts() {
    List<Product> allProducts = productRepository.findAll();
    log.info("Generando alertas para {} productos...", allProducts.size());

    allProducts.forEach(
        product ->
            updateAlertForProduct(
                product.getId(),
                product.getName(),
                product.getCurrentStock(),
                product.getMinimumStock()));

    log.info("Generación de alertas completada.");
  }

  @Override
  public void updateAlertForProduct(
      String productId, String productName, int currentStock, int minimumStock) {

    if (currentStock <= minimumStock) {
      AlertSeverity severity = (currentStock == 0) ? AlertSeverity.CRITICAL : AlertSeverity.WARNING;

      StockAlert alert =
          stockAlertRepository
              .findByProductId(productId)
              .orElse(StockAlert.builder().productId(productId).acknowledged(false).build());

      // Si la severidad cambió (ej: WARNING → CRITICAL), resetear acknowledged
      if (alert.isAcknowledged() && alert.getSeverity() != severity) {
        alert.setAcknowledged(false);
        alert.setAcknowledgedBy(null);
        alert.setAcknowledgedAt(null);
      }

      alert.setProductName(productName);
      alert.setCurrentStock(currentStock);
      alert.setMinimumStock(minimumStock);
      alert.setSeverity(severity);

      stockAlertRepository.save(alert);
      log.debug(
          "Alerta {} para producto '{}' (stock: {}/{})",
          severity.getValue(),
          productName,
          currentStock,
          minimumStock);

    } else {
      // Stock recuperado → eliminar alerta si existe
      stockAlertRepository
          .findByProductId(productId)
          .ifPresent(
              alert -> {
                stockAlertRepository.delete(alert);
                log.debug(
                    "Alerta eliminada para producto '{}' (stock recuperado: {})",
                    productName,
                    currentStock);
              });
    }
  }
}
