package com.inventario.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
  ADMIN("admin"),
  WAREHOUSE_MANAGER("warehouse_manager"),
  VIEWER("viewer");

  private final String value;

  Role(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static Role fromValue(String value) {
    for (Role role : values()) {
      if (role.value.equalsIgnoreCase(value) || role.name().equalsIgnoreCase(value)) {
        return role;
      }
    }
    throw new IllegalArgumentException("Rol inválido: " + value);
  }
}
