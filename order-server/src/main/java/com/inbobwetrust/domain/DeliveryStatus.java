package com.inbobwetrust.domain;

public enum DeliveryStatus {
    NEW("접수요청", "접수대기중"),

    ACCEPTED("배차완료", "접수완료"),

    DECLINED("접수", "접수"),

    PICKED_UP("픽업완료", "픽업완료"),

    COMPLETE("배달완료", "배달완료");

    private final String shopPerspective;
    private final String customerPerspective;

    DeliveryStatus(String shopPerspective, String customerPerspective) {
        this.shopPerspective = shopPerspective;
        this.customerPerspective = customerPerspective;
    }
}
