package com.inventario.backend.controller;

import com.inventario.backend.dto.response.ApiResponse;
import com.inventario.backend.dto.response.StockAlertResponse;
import com.inventario.backend.model.AlertSeverity;
import com.inventario.backend.service.StockAlertService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class StockAlertController {

  private final StockAlertService stockAlertService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<StockAlertResponse>>> findAll(
      @RequestParam(required = false) Boolean acknowledged,
      @RequestParam(required = false) AlertSeverity severity) {
    return ResponseEntity.ok(ApiResponse.ok(stockAlertService.findAll(acknowledged, severity)));
  }

  @PatchMapping("/{id}/acknowledge")
  public ResponseEntity<ApiResponse<StockAlertResponse>> acknowledge(
      @PathVariable String id, Authentication authentication) {
    // Se pasa el email; el servicio lo usa como acknowledgedBy
    StockAlertResponse response = stockAlertService.acknowledge(id, authentication.getName());
    return ResponseEntity.ok(ApiResponse.ok("Alerta confirmada exitosamente", response));
  }

  @PostMapping("/generate")
  public ResponseEntity<ApiResponse<Void>> generate() {
    stockAlertService.generateAlerts();
    return ResponseEntity.ok(ApiResponse.ok("Alertas generadas exitosamente", null));
  }
}
