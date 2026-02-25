package com.inventario.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Unit {
  PIECE("piece"),
  BOARD("board"),
  KG("kg"),
  LITER("liter"),
  METER("meter"),
  BOX("box"),
  SHEET("sheet");

  private final String value;

  Unit(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static Unit fromValue(String value) {
    for (Unit unit : values()) {
      if (unit.value.equalsIgnoreCase(value) || unit.name().equalsIgnoreCase(value)) {
        return unit;
      }
    }
    throw new IllegalArgumentException("Unidad inválida: " + value);
  }
}
