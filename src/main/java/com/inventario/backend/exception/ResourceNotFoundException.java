package com.inventario.backend.exception;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String resource, String id) {
    super(resource + " no encontrado con id: " + id);
  }
}
