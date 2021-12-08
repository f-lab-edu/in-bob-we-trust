package com.inbobwetrust.producer;

import com.inbobwetrust.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class LocalOrderProducerImplTest {

    @Autowired OrderProducer orderProducer;

    @Mock OrderService orderService;

    @Test
    @DisplayName("OrderService.receiveNewOrder() 성공으로 이벤트 발행 1회")
    void sendNewOrderMessage_successTest() {
    }

    @Test
    @DisplayName("OrderService.receiveNewOrder() 실패로 이벤트 미발행 1회")
    void sendNewOrderMessage_failTest() {}
}
