package com.inventario.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MovementReason {
  PURCHASE("purchase"),
  PRODUCTION("production"),
  SALE("sale"),
  ADJUSTMENT("adjustment"),
  RETURN("return");

  private final String value;

  MovementReason(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static MovementReason fromValue(String value) {
    for (MovementReason reason : values()) {
      if (reason.value.equalsIgnoreCase(value) || reason.name().equalsIgnoreCase(value)) {
        return reason;
      }
    }
    throw new IllegalArgumentException("Razón de movimiento inválida: " + value);
  }
}
