package com.inventario.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AlertSeverity {
  CRITICAL("critical"),
  WARNING("warning");

  private final String value;

  AlertSeverity(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static AlertSeverity fromValue(String value) {
    for (AlertSeverity severity : values()) {
      if (severity.value.equalsIgnoreCase(value) || severity.name().equalsIgnoreCase(value)) {
        return severity;
      }
    }
    throw new IllegalArgumentException("Severidad inválida: " + value);
  }
}
