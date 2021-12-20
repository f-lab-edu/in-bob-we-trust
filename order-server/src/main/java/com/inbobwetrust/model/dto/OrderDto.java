package com.inbobwetrust.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class OrderDto {
    @NotNull(message = "주문번호는 필수값입니다.")
    @Min(value = 1, message = "주문번호는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long id;

    @NotNull(message = "주문번호는 필수값입니다.")
    @Min(value = 1, message = "주문번호는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long customerId;

    @NotNull(message = "주문번호는 필수값입니다.")
    @Min(value = 1, message = "주문번호는 최소 1 이상이어야 합니다.")
    @Max(Long.MAX_VALUE)
    private Long shopId;

    @NotBlank(message = "배달주소는 필수값입니다.")
    private String address;

    @NotBlank(message = "전화번호는 필수값입니다.")
    private String phoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @NotNull(message = "주문일시는 필수값입니다.")
    private LocalDateTime createdAt;

    @Builder
    public OrderDto(
            Long id,
            Long customerId,
            Long shopId,
            String address,
            String phoneNumber,
            LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.shopId = shopId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }
}
