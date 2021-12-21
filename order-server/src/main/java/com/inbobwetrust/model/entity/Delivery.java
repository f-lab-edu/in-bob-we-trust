package com.inbobwetrust.model.entity;

import com.inbobwetrust.model.dto.DeliveryStatusDto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode
public class Delivery {
    private Long id;

    private Long orderId;

    private Long riderId;

    private Long agencyId;

    private OrderStatus orderStatus = OrderStatus.NEW;

    private LocalDateTime pickupTime;

    private LocalDateTime finishTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Delivery(
            Long id,
            Long orderId,
            Long riderId,
            Long agencyId,
            OrderStatus orderStatus,
            LocalDateTime pickupTime,
            LocalDateTime finishTime,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.riderId = riderId;
        this.agencyId = agencyId;
        this.orderStatus = orderStatus;
        this.pickupTime = pickupTime;
        this.finishTime = finishTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Delivery deepCopy() {
        return Delivery.builder()
                .id(id)
                .orderId(orderId)
                .riderId(riderId)
                .agencyId(agencyId)
                .orderStatus(orderStatus)
                .pickupTime(pickupTime)
                .finishTime(finishTime)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public DeliveryStatusDto toDeliveryStatus() {
        return DeliveryStatusDto.builder()
                .orderId(this.orderId)
                .orderStatus(this.orderStatus)
                .build();
    }

    public boolean isNew() {
        if (orderStatus == null) orderStatus = OrderStatus.NEW;
        return orderStatus.equals(OrderStatus.NEW);
    }

    public boolean isValidPickupTime() {
        return pickupTime.compareTo(LocalDateTime.now()) == 1;
    }

    public boolean matchesRider(Rider rider) {
        if (this.riderId == null || this.agencyId == null || rider == null || !rider.hasAllData()) {
            return false;
        }
        return (this.riderId.longValue() == rider.getId().longValue())
                && (this.agencyId.longValue() == rider.getAgencyId().longValue());
    }

    public boolean canSetRider() {
        if (orderStatus == null) return false;
        boolean statusCan = orderStatus.equals(OrderStatus.ACCEPTED);
        boolean finishTimeCan = finishTime != null;
        return statusCan && finishTimeCan;
    }
}
