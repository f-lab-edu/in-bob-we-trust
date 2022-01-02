package com.inbobwetrust.exception;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
  private static final String VALIDATION_ERROR_DELIMITER = "/";

  @ExceptionHandler(value = {WebExchangeBindException.class})
  public ResponseEntity<String> handleConstraintViolation(WebExchangeBindException ex) {
    logException(ex);
    var error =
        ((WebExchangeBindException) ex)
            .getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .sorted()
                .collect(Collectors.joining(VALIDATION_ERROR_DELIMITER));
    log.error("Error is : {}", error);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(value = {IllegalArgumentException.class, DeliveryNotFoundException.class})
  public ResponseEntity<String> handleBadRequests(Exception ex) {
    logException(ex);
    var error = ex.getMessage();
    log.error("Error is : {}", error);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(value = {RelayClientException.class})
  public ResponseEntity<String> handleShopConnectionFailedException(RelayClientException ex) {
    logException(ex);
    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(ex.getMessage());
  }

  @ExceptionHandler(value = {RelayServerException.class})
  public ResponseEntity<String> handleRelayServerException(RelayServerException ex) {
    logException(ex);
    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(ex.getMessage());
  }

  private void logException(Throwable ex) {
    log.error("{} caught by advice : {} ", ex.getClass().getName(), ex.getMessage(), ex);
  }
}
