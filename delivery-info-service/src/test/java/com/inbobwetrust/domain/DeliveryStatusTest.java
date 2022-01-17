package com.inbobwetrust.domain;

import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DeliveryStatusTest {

  static final Logger LOG = LoggerFactory.getLogger(DeliveryStatusTest.class);

  @Test
  void canProceedTo() {
    for (DeliveryStatus fromStatus : DeliveryStatus.values()) {
      for (DeliveryStatus toStatus : DeliveryStatus.values()) {
        LOG.info(
            "testing from {}[{}] to {}[{}]",
            fromStatus.ordinal(),
            fromStatus,
            toStatus.ordinal(),
            toStatus);
        if (!fromStatus.equals(DeliveryStatus.DECLINED)
            && (fromStatus.ordinal() + 1) == toStatus.ordinal()) {
          LOG.info("asserting true");
          assertTrue(fromStatus.canProceedTo(toStatus));
        } else {
          LOG.info("asserting false");
          assertFalse(fromStatus.canProceedTo(toStatus));
        }
      }
    }
  }

  @Test
  void getNext_isLast() {
    var statusList = DeliveryStatus.values();
    for (var status : statusList) {
      if (status.ordinal() != statusList.length - 1) {
        assertEquals(status.getNext(), statusList[status.ordinal() + 1]);
      } else {
        assertEquals(status, status.getNext());
      }
    }
  }
}
