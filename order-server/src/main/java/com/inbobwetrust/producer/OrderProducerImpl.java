package com.inbobwetrust.producer;

import com.inbobwetrust.model.entity.Order;
import com.inbobwetrust.service.EndpointService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrderProducerImpl implements OrderProducer {
    private WebClient webClient;
    private final EndpointService endpointService;

    public OrderProducerImpl(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @Override
    public Order sendNewOrderMessage(Order order) {
        return getClient()
                .post()
                .uri(endpointService.findShopEndpoint(order))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(order))
                .retrieve()
                .bodyToMono(Order.class)
                .block();
    }

    private WebClient getClient() {
        if (this.webClient == null) this.webClient = WebClient.create();
        return this.webClient;
    }
}
