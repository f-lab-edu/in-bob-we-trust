package com.inbobwetrust.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@Document
@AllArgsConstructor
@Builder
public class Delivery {
  @Id private String id;

  private String orderId;

  private String riderId;

  private String agencyId;

  @NotBlank(message = "고객님의 아이디는 필수 입력값입니다.")
  private String customerId;

  @NotBlank(message = "배달주소는 필수 입력값입니다.")
  private String address;

  @NotBlank(message = "전화번호는 필수 입력값입니다.")
  private String phoneNumber;

  private String comment;

  private DeliveryStatus deliveryStatus;

  @NotNull(message = "주문일시는 필수 입력값입니다.")
  private LocalDateTime orderTime;

  private LocalDateTime pickupTime;

  private LocalDateTime finishTime;
}
