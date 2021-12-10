package com.inbobwetrust.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbobwetrust.model.vo.Delivery;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.SocketUtils;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class DeliveryProducerImplTest {
    DeliveryProducerImpl deliveryProducer = new DeliveryProducerImpl();
    MockWebServer server;
    String uri;

    @BeforeEach
    void setup() throws IOException {
        server = new MockWebServer();
        server.start(SocketUtils.findAvailableTcpPort());
        uri =
                new StringBuilder()
                        .append("http://")
                        .append(server.getHostName())
                        .append(":")
                        .append(server.getPort())
                        .toString();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    @DisplayName("사장님에게 접수요청 전달 성공")
    public void sendAddDeliveryMessage() throws IOException, URISyntaxException {
        Delivery delivery = new Delivery(uri, "hi");
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(new ObjectMapper().writeValueAsString(delivery)));

        Delivery del = deliveryProducer.sendAddDeliveryMessage(delivery);
        log.info(delivery.toString());
        assertEquals(delivery.getShopIp(), del.getShopIp());
        assertEquals(delivery.getOrderId(), del.getOrderId());
        assertEquals(1, server.getRequestCount());
    }

    @Test
    @DisplayName("사장님에게 접수요청 전달 실패 w/ retry")
    public void sendAddDeliveryMessage_failTest() throws IOException, URISyntaxException {
        int retries = 10;
        deliveryProducer.setRetries(retries);
        Delivery delivery = new Delivery(uri, "hi");
        for (int i = 0; i < retries; i++) {}

        Delivery del = deliveryProducer.sendAddDeliveryMessage(delivery);
        assertEquals(delivery.getShopIp(), del.getShopIp());
        assertEquals(delivery.getOrderId(), del.getOrderId());
        assertEquals(retries, server.getRequestCount());
    }
}
