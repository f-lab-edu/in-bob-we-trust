package com.inbobwetrust.producer.local.consumer;

import com.inbobwetrust.producer.local.consumer.adddelivery.DeliveryAgents;
import com.inbobwetrust.util.vo.DeliveryInstanceGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeliveryAgentsTest {

    @Test
    @DisplayName("배달대행사에게 배차요청 전달 성공")
    void receiveTest() {
        DeliveryAgents deliveryAgents = new DeliveryAgents();
        int totalCount = 1000;
        for (int i = 0; i < totalCount; i++) {
            deliveryAgents.receive(DeliveryInstanceGenerator.makeSimpleNumberedDelivery(i));
        }

        assertEquals(totalCount, deliveryAgents.agentsCount());
        assertEquals(totalCount, deliveryAgents.getHistory().size());
    }
}
