package com.inbobwetrust.producer.local.consumer;

import com.inbobwetrust.model.vo.Order;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Shops {
    private static final Map<String, List<Order>> shopIdToOrderlist = new HashMap<>();
    private static final List<Order> history = new ArrayList<>();

    public void send(Order order) {
        log.info("shops send {}", shopIdToOrderlist);
        List<Order> shopOrders =
                shopIdToOrderlist.getOrDefault(order.getShopId(), new ArrayList<>());
        shopOrders.add(order);
        this.shopIdToOrderlist.put(order.getShopId(), shopOrders);
        history.add(order);
    }

    public List<Order> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public int shopsCount() {
        return shopIdToOrderlist.keySet().size();
    }
}