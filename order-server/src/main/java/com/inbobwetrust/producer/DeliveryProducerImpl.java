package com.inbobwetrust.producer;

import com.inbobwetrust.model.vo.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URISyntaxException;
import java.time.Duration;

@Component
@Slf4j
public class DeliveryProducerImpl implements DeliveryProducer<Delivery> {
    private WebClient webClient;

    private int retries = 3;

    public DeliveryProducerImpl() {
        webClient = WebClient.create();
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    @Override
    public Delivery sendAddDeliveryMessage(Delivery delivery) throws URISyntaxException {
        return webClient
                .post()
                .uri(delivery.getShopIp())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(delivery))
                .retrieve()
                .bodyToMono(Delivery.class)
                .timeout(Duration.ofSeconds(1))
                .retry(retries)
                .block();
    }
}
