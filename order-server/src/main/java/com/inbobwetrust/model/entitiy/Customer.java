package com.inbobwetrust.model.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class Customer {
    private String id;

    private String name;

    private String password;

    private String tel;

    private String email;

    private String address;

    private Long point;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    @Builder
    public Customer(String id, String name, String password, String tel, String email, String address, Long point,
                    LocalDateTime regDate, LocalDateTime modDate) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.tel = tel;
        this.email = email;
        this.address = address;
        this.point = point;
        this.regDate = regDate;
        this.modDate = modDate;
    }
}
