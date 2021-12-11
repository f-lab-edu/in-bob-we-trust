package com.inbobwetrust.model.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class Menu {
    private Long id;

    private Long shopId;

    private String name;

    private Long price;

    private String description;

    private String photo;

    @Builder
    public Menu(Long id, Long shopId, String name, Long price, String description, String photo) {
        this.id = id;
        this.shopId = shopId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.photo = photo;
    }
}
