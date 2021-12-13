package com.inbobwetrust.config.swaggerdoc;

import com.inbobwetrust.model.vo.Order;
import org.springframework.http.ResponseEntity;

public interface OrderControllerSwaggerDoc {
    ResponseEntity<Order> receiveNewOrder(Order newOrder);
}
