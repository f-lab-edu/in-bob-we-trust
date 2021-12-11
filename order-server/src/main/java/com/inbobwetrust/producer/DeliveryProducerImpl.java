package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class DeliveryProducerImpl implements DeliveryProducer {
    private WebClient webClient;

    public DeliveryProducerImpl() {
        webClient = WebClient.create();
    }

    @Override
    public Delivery sendAddDeliveryMessage(Delivery delivery) {
        return webClient
                .post()
                .uri(delivery.getShopIp())
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(delivery))
                .retrieve()
                .bodyToMono(Delivery.class)
                .block();
    }

    @Override
    public void sendSetRiderMessage(Delivery updatedDelivery) {
        throw new RuntimeException("unimplemented");
    }
}
