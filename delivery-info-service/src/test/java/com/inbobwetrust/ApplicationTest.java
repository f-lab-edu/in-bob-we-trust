package com.inbobwetrust;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;

public class ApplicationTest {

  @Test
  void mainTest() {
    Assertions.assertThrows(
        UnsatisfiedDependencyException.class, () -> Application.main(new String[] {}));
  }
}
