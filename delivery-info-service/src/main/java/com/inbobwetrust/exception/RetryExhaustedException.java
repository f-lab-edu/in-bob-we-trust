package com.inbobwetrust.exception;

import reactor.util.retry.Retry;

public class RetryExhaustedException extends RuntimeException {
  public RetryExhaustedException(Retry.RetrySignal retrySignal) {
    super(retrySignal.failure());
  }
}
