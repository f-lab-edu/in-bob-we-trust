package com.inbobwetrust;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ApplicationTest {

  @Test
  void mainTest() {
    Assertions.assertDoesNotThrow(
        () -> Application.main(new String[] {}));
  }
}
