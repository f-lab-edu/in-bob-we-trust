package com.inbobwetrust.producer.local.consumer.adddelivery;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.producer.local.consumer.ExternalMessageConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DeliveryAgents implements ExternalMessageConsumer<Delivery> {
    private static final Map<String, List<Delivery>> deliveryAgents = new HashMap<>();
    private static final List<Delivery> history = new ArrayList<>();

    @Override
    public void receive(Delivery message) {
        log.info("DeliveryAgents received ({})", message);
        List<Delivery> deliveryList =
                deliveryAgents.getOrDefault(message.getDeliveryAgentId(), new ArrayList<>());
        deliveryList.add(message);
        this.deliveryAgents.put(message.getDeliveryAgentId(), deliveryList);
        history.add(message);
    }

    public List<Delivery> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public int agentsCount() {
        return deliveryAgents.keySet().size();
    }
}
