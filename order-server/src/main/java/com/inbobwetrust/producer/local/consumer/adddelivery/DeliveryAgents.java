package com.inbobwetrust.producer.local.consumer.adddelivery;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.producer.local.consumer.ExternalMessageConsumer;

import java.util.*;

public class DeliveryAgents implements ExternalMessageConsumer<Delivery> {
    private static final Map<String, List<Delivery>> deliveryAgents = new HashMap<>();
    private static final List<Delivery> history = new ArrayList<>();

    @Override
    public void receive(Delivery message) {
        List<Delivery> deliveryList =
                deliveryAgents.getOrDefault(message.getDeliveryAgentId(), new ArrayList<>());
        deliveryList.add(message);
        this.deliveryAgents.put(message.getDeliveryAgentId(), deliveryList);
        history.add(message);
        System.out.println(deliveryAgents);
    }

    public List<Delivery> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public int shopsCount() {
        return deliveryAgents.keySet().size();
    }
}
