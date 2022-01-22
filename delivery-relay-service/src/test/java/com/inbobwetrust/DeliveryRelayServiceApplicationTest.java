package com.inbobwetrust;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.SocketUtils;

public class DeliveryRelayServiceApplicationTest {
  int serverPort;

  @BeforeEach
  void setUp() {
    System.getProperties().put("server.port", SocketUtils.findAvailableTcpPort());
  }

  @Test
  void defaultProfileTest() {
    Assertions.assertDoesNotThrow(
        () -> {
          DeliveryRelayServiceApplication.main(new String[] {});
        });
  }

  @Test
  void productionProfileTest() {
    Assertions.assertDoesNotThrow(
        () -> {
          DeliveryRelayServiceApplication.main(
              new String[] {"--spring.profiles.active=production"});
        });
  }

  @Test
  void localProfileTest() {
    Assertions.assertDoesNotThrow(
        () -> {
          DeliveryRelayServiceApplication.main(new String[] {"--spring.profiles.active=local"});
        });
  }

  @Test
  void testProfileTest() {
    Assertions.assertDoesNotThrow(
        () -> {
          DeliveryRelayServiceApplication.main(new String[] {"--spring.profiles.active=test"});
        });
  }
}
