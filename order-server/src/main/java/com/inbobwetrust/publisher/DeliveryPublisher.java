package com.inbobwetrust.publisher;

import com.inbobwetrust.domain.Delivery;

public interface DeliveryPublisher {

  void sendAddDeliveryEvent(Delivery delivery);
}
