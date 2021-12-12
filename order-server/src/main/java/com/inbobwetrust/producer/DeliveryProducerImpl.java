package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.service.EndpointService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DeliveryProducerImpl implements DeliveryProducer {
    private WebClient webClient;
    private final EndpointService endpointService;

    public DeliveryProducerImpl(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @Override
    public Delivery sendAddDeliveryMessage(Delivery delivery) {
        return getWebClient()
                .post()
                .uri(endpointService.findDeliveryAgentEndpoint(delivery))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(delivery))
                .retrieve()
                .bodyToMono(Delivery.class)
                .block();
    }

    private WebClient getWebClient() {
        if (this.webClient == null) this.webClient = WebClient.create();
        return this.webClient;
    }
}
