package com.inbobwetrust.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.service.EndpointService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.SocketUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryProducerImplTest {
    @InjectMocks DeliveryProducerImpl producer;
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
    @DisplayName("사장님에게 접수요청 전달 성공")
    public void sendAddDeliveryMessage_successTest() throws Exception {
        Delivery delivery = Delivery.builder().orderId("order-1").build();
        Mockito.when(endpointService.findDeliveryAgentEndpoint(delivery)).thenReturn(uri);
        server.enqueue(successfulResponse(delivery));

        Delivery responseDelivery = producer.sendAddDeliveryMessage(delivery);

        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals(uri, recordedRequest.getRequestUrl().toString());
        assertEquals(
                delivery,
                mapper.readValue(recordedRequest.getBody().readByteArray(), Delivery.class));
        assertEquals(1, server.getRequestCount());
        assertEquals(delivery.getOrderId(), responseDelivery.getOrderId());
    }

    @Test
    @DisplayName("사장님에게 접수요청 전달 : 실패 (4xx 와 5xx 리턴")
    void sendAddDeliveryMessage_failTest_error() {
        Delivery delivery = Delivery.builder().orderId("order-1").build();
        Mockito.when(endpointService.findDeliveryAgentEndpoint(delivery)).thenReturn(uri);
        for (int i = 0; i < 10; i++) {
            server.enqueue(failResponse(HttpStatus.BAD_REQUEST));
            assertThrows(
                    WebClientResponseException.class,
                    () -> producer.sendAddDeliveryMessage(delivery));
            server.enqueue(failResponse(HttpStatus.INTERNAL_SERVER_ERROR));
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

    private MockResponse failResponse(HttpStatus statusCode) {
        return new MockResponse().setResponseCode(statusCode.value());
    }
}
