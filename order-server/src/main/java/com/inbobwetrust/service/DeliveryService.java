package com.inbobwetrust.service;

import com.inbobwetrust.exceptions.EmptyResultSetSqlException;
import com.inbobwetrust.exceptions.NoAffectedRowsSqlException;
import com.inbobwetrust.model.dto.DeliveryStatusDto;
import com.inbobwetrust.model.entity.Delivery;
import com.inbobwetrust.model.entity.Rider;
import com.inbobwetrust.producer.DeliveryProducer;
import com.inbobwetrust.repository.DeliveryRepository;

import com.inbobwetrust.repository.RiderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final RiderRepository riderRepository;
    private final DeliveryProducer deliveryProducer;

    public Delivery addDelivery(Delivery delivery) {
        canAddDelivery(delivery);
        addEstimatedDeliveryFinishTime(delivery);
        saveOrThrow(delivery, "[신규주문접수] 실패");
        deliveryProducer.sendAddDeliveryMessage(delivery);
        return delivery;
    }

    private void canAddDelivery(Delivery delivery) {
        if (delivery == null) throw new IllegalArgumentException("[신규주문접수] 배달 정보가 누락되었습니다.");
        if (!delivery.isNew())
            throw new IllegalStateException(
                    "신규 접수건이 아닙니다. 주문상태 : ".concat(delivery.getOrderStatus().toString()));
        if (!delivery.isValidPickupTime())
            throw new IllegalStateException("픽업요청이 유효성검사 시간보다 일찍입니다.");
        if (delivery.getFinishTime() != null)
            throw new IllegalStateException("[배달완료시간 사전설정] 배달완료시간은 어플리케이션에서 설정해야합니다.");
    }

    public void addEstimatedDeliveryFinishTime(Delivery delivery) {
        delivery.setFinishTime(delivery.getPickupTime().plusMinutes(30));
    }

    public Delivery setRider(Delivery delivery) {
        canSetRider(delivery);
        updateOrThrow(delivery, "setRider() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        return updatedDelivery;
    }

    private void canSetRider(Delivery delivery) {
        if (delivery == null) throw new IllegalArgumentException("[라이더배정] 정보가 누락되었습니다.");

        if (!delivery.canSetRider())
            throw new IllegalStateException("[라이더배정] 라이더 배정가능한 주문상태가 아닙니다.");

        if (!matchingDeliveryAndRider(delivery))
            throw new IllegalArgumentException("[라이더배정] 등록된 라이더의 정보와 일치하지 않습니다.");

        if (alreadySetRider(delivery)) {
            throw new IllegalStateException("[라이더배정] 이미 라이더가 배정된 주문입니다.");
        }
    }

    private boolean alreadySetRider(Delivery aDelivery) {
        Delivery existing = findByOrderId(aDelivery.getOrderId());
        return existing.getRiderId() != null;
    }

    private boolean matchingDeliveryAndRider(Delivery delivery) {
        Rider rider = findRiderById(delivery.getRiderId());
        return delivery.matchesRider(rider);
    }

    private Rider findRiderById(Long riderId) {
        return riderRepository.findByRiderId(riderId).orElseThrow(EmptyResultSetSqlException::new);
    }

    public Delivery setStatusPickup(Delivery delivery) {
        updateOrThrow(delivery, "setStatusPickup() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        return updatedDelivery;
    }

    public Delivery setStatusComplete(Delivery delivery) {
        updateOrThrow(delivery, "setStatusComplete() Failed : No Such OrderId");
        Delivery updatedDelivery = findByOrderId(delivery.getOrderId());
        return updatedDelivery;
    }

    public DeliveryStatusDto findDeliveryStatusByOrderId(Long orderId) {
        return deliveryRepository
                .findDeliveryStatusByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No such delivery associated with Id"));
    }

    private void saveOrThrow(Delivery delivery, String msg) {
        if (!deliveryRepository.save(delivery)) {
            throw new NoAffectedRowsSqlException();
        }
    }

    private void updateOrThrow(Delivery delivery, String msg) {
        if (!deliveryRepository.update(delivery)) {
            throw new NoAffectedRowsSqlException();
        }
    }

    private Delivery findByOrderId(Long orderId) {
        return deliveryRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Cannot find Delivery"));
    }
}
