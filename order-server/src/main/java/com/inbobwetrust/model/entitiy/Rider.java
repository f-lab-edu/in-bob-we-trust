package com.inbobwetrust.model.entitiy;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class Rider {
    private String id;

    private Long agencyId;

    private String name;

    private String password;

    private RiderStatus status;

    private String tel;

    private String email;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

    @Builder
    public Rider(
            String id,
            Long agencyId,
            String name,
            String password,
            RiderStatus status,
            String tel,
            String email,
            LocalDateTime regDate,
            LocalDateTime modDate) {
        this.id = id;
        this.agencyId = agencyId;
        this.name = name;
        this.password = password;
        this.status = status;
        this.tel = tel;
        this.email = email;
        this.regDate = regDate;
        this.modDate = modDate;
    }
}
