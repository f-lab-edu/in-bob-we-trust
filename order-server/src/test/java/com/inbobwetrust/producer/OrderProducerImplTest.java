package com.inbobwetrust.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbobwetrust.model.entity.Order;
import com.inbobwetrust.service.EndpointService;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.SocketUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class OrderProducerImplTest {
    @InjectMocks OrderProducerImpl producer;
    @Mock EndpointService endpointService;
    MockWebServer server;
    String uri;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() throws IOException {
        server = new MockWebServer();
        server.start(SocketUtils.findAvailableTcpPort());
        uri = buildServerUri();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    private String buildServerUri() {
        return new StringBuilder()
                .append("http://")
                .append(server.getHostName())
                .append(":")
                .append(server.getPort())
                .append("/")
                .toString();
    }

    @Test
    @DisplayName("배달대행사 라이더배정요청 성공")
    void sendNewOrderMessage_success() throws IOException, InterruptedException {
        Order order = Order.builder().id(1L).build();
        Mockito.when(endpointService.findShopEndpoint(order)).thenReturn(uri);
        server.enqueue(successfulResponse(order));

        Order responseOrder = producer.sendNewOrderMessage(order);

        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals(uri, recordedRequest.getRequestUrl().toString());
        assertEquals(
                order, mapper.readValue(recordedRequest.getBody().readByteArray(), Order.class));
        assertEquals(1, server.getRequestCount());
        assertEquals(order.getId(), responseOrder.getId());
    }

    @Test
    @DisplayName("배달대행사에게 라이더배정 요청 실패")
    void sendNewOrderMessage_fail() {
        Order order = Order.builder().id(1L).build();
        Mockito.when(endpointService.findShopEndpoint(order)).thenReturn(uri);

        for (int i = 0; i < 10; i++) {
            server.enqueue(failResponse(HttpStatus.BAD_REQUEST));
            assertThrows(
                    WebClientResponseException.class, () -> producer.sendNewOrderMessage(order));
            server.enqueue(failResponse(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThrows(
                    WebClientResponseException.class, () -> producer.sendNewOrderMessage(order));
        }
    }

    private MockResponse successfulResponse(Order body) throws JsonProcessingException {
        return new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(mapper.writeValueAsString(body));
    }

    private MockResponse failResponse(HttpStatus statusCode) {
        return new MockResponse().setResponseCode(statusCode.value());
    }
}
