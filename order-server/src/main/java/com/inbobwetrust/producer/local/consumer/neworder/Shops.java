package com.inbobwetrust.producer.local.consumer.neworder;

import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.producer.local.consumer.ExternalMessageConsumer;

import java.util.*;

public class Shops implements ExternalMessageConsumer<Order> {
    private static final Map<String, List<Order>> shopIdToOrderlist = new HashMap<>();
    private static final List<Order> history = new ArrayList<>();

    @Override
    public void receive(Order message) {
        System.out.println(shopIdToOrderlist);
        List<Order> shopOrders =
                shopIdToOrderlist.getOrDefault(message.getShopId(), new ArrayList<>());
        shopOrders.add(message);
        this.shopIdToOrderlist.put(message.getShopId(), shopOrders);
        history.add(message);
    }

    public List<Order> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public int shopsCount() {
        return shopIdToOrderlist.keySet().size();
    }
}
