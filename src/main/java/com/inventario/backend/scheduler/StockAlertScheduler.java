package com.inventario.backend.scheduler;

import com.inventario.backend.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockAlertScheduler {

  private final StockAlertService stockAlertService;

  // Ejecuta cada hora (3 600 000 ms)
  @Scheduled(fixedRate = 3_600_000)
  public void runAlertGeneration() {
    log.info("Scheduler: iniciando generación periódica de alertas de stock...");
    stockAlertService.generateAlerts();
    log.info("Scheduler: generación de alertas completada.");
  }
}
