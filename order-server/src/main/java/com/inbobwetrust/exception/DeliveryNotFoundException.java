package com.inbobwetrust.exception;

public class DeliveryNotFoundException extends RuntimeException {
  private String message;

  public DeliveryNotFoundException() {
    super("요청한 배달건의 정보를 찾을 수 없습니다.");
  }
}
