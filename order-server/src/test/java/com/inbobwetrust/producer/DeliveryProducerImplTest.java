package com.inbobwetrust.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbobwetrust.model.vo.Delivery;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.util.SocketUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class DeliveryProducerImplTest {
    DeliveryProducerImpl producer = new DeliveryProducerImpl();
    MockWebServer server;
    String uri;
    ObjectMapper mapper = new ObjectMapper();

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
                        .append("/")
                        .toString();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    @DisplayName("사장님에게 접수요청 전달 성공")
    public void sendAddDeliveryMessage() throws Exception {
        Delivery delivery = Delivery.builder().shopIp(uri).orderId("hi").build();
        server.enqueue(successfulResponse(delivery));

        Delivery responseDelivery = producer.sendAddDeliveryMessage(delivery);

        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals(uri, recordedRequest.getRequestUrl().toString());
        assertEquals(
                delivery,
                mapper.readValue(recordedRequest.getBody().readByteArray(), Delivery.class));
        assertEquals(1, server.getRequestCount());
        assertEquals(delivery.getShopIp(), responseDelivery.getShopIp());
        assertEquals(delivery.getOrderId(), responseDelivery.getOrderId());
    }

    @Test
    @DisplayName("에러 응답")
    void sendAddDeliveryMessage_failTest_error() throws Exception {
        Delivery delivery = Delivery.builder().shopIp(uri).orderId("hi").build();
        for (int i = 0; i < 10; i++) {
            server.enqueue(failResponseOfStatus(HttpStatus.BAD_REQUEST));
            assertThrows(
                    WebClientResponseException.class,
                    () -> producer.sendAddDeliveryMessage(delivery));
            server.enqueue(failResponseOfStatus(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThrows(
                    WebClientResponseException.class,
                    () -> producer.sendAddDeliveryMessage(delivery));
        }
    }

    private MockResponse successfulResponse(Delivery body) throws JsonProcessingException {
        return new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mapper.writeValueAsString(body));
    }

    private MockResponse failResponseOfStatus(HttpStatus statusCode) {
        return new MockResponse().setResponseCode(statusCode.value());
    }
}
