package com.inbobwetrust.publisher;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfigureWireMock(port = 0)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeliveryPublisherImplTest {
  DeliveryPublisherImpl deliveryPublisher;

  @BeforeAll
  void setUp() {
    var webClient = new PublisherConfig().webClient(WebClient.builder());
    var proxyShopUrl = "";
    var proxyAgencyUrl = "";
    deliveryPublisher = new DeliveryPublisherImpl(webClient, proxyShopUrl, proxyAgencyUrl);
  }

  @Test
  void sendAddDeliveryEvent() {
    // given

    // when
    // then

  }

  @Test
  void sendSetRiderEvent() {
    // given
    // when

    // then

  }
}
