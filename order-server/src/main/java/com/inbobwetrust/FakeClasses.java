package com.inbobwetrust;

import com.inbobwetrust.model.vo.Delivery;
import com.inbobwetrust.model.vo.DeliveryStatus;
import com.inbobwetrust.model.vo.Order;
import com.inbobwetrust.model.vo.Rider;
import com.inbobwetrust.repository.DeliveryRepository;
import com.inbobwetrust.repository.OrderRepository;
import com.inbobwetrust.repository.RiderRepository;
import com.inbobwetrust.service.EndpointService;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

public class FakeClasses {}

@Component
class d implements EndpointService {

    @Override
    public String findShopEndpoint(Order order) {
        return null;
    }

    @Override
    public String findDeliveryAgentEndpoint(Delivery delivery) {
        return null;
    }
}

@Component
class c implements RiderRepository {

    @Override
    public boolean save(Rider rider) {
        return false;
    }

    @Override
    public boolean update(Rider rider) {
        return false;
    }

    @Override
    public Optional<Rider> findByRiderId(String riderId) {
        return Optional.empty();
    }

    @Override
    public List<Rider> findAll() {
        return null;
    }
}

@Component
class b implements OrderRepository {

    @Override
    public boolean save(Order order) {
        return false;
    }

    @Override
    public Optional<Order> findByOrderId(String id) {
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        return null;
    }
}

@Component
class a implements DeliveryRepository {

    @Override
    public boolean save(Delivery delivery) {
        return false;
    }

    @Override
    public Optional<Delivery> findByOrderId(String orderId) {
        return Optional.empty();
    }

    @Override
    public List<Delivery> findAll() {
        return null;
    }

    @Override
    public boolean update(Delivery delivery) {
        return false;
    }

    @Override
    public Optional<DeliveryStatus> findDeliveryStatusByOrderId(String orderId) {
        return Optional.empty();
    }
}
