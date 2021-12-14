package com.inbobwetrust.model.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class Shop {
    private Long id;

    private String name;

    private String ownerName;

    private String tel;

    private String address;

    private ShopStatus status;

    private String photo;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    private LocalDateTime workStartTime;

    private LocalDateTime workEndTime;

    @Builder
    public Shop(
            Long id,
            String name,
            String ownerName,
            String tel,
            String address,
            ShopStatus status,
            String photo,
            LocalDateTime regDate,
            LocalDateTime modDate,
            LocalDateTime workStartTime,
            LocalDateTime workEndTime) {
        this.id = id;
        this.name = name;
        this.ownerName = ownerName;
        this.tel = tel;
        this.address = address;
        this.status = status;
        this.photo = photo;
        this.regDate = regDate;
        this.modDate = modDate;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
    }
}
