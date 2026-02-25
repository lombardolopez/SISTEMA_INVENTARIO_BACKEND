package com.inventario.backend.exception;

public class BusinessException extends RuntimeException {

  private final int statusCode;

  public BusinessException(String message) {
    super(message);
    this.statusCode = 400;
  }

  public BusinessException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
