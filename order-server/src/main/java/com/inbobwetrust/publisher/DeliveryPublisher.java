package com.inbobwetrust.publisher;

import com.inbobwetrust.domain.Delivery;

public interface DeliveryPublisher {

  Delivery sendAddDeliveryEvent(Delivery delivery);
}
