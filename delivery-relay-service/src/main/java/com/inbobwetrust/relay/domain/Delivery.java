package com.inbobwetrust.relay.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Document
public class Delivery {
  @NotNull(message = "배달 고유 번호는 필수 입력값입니다.")
  @Id
  private String Id;

  @NotNull(message = "주문번호는 필수 입력값입니다.")
  private String orderId;

  private String riderId;

  private String agencyId;

  @NotBlank(message = "사장님 아이디는 필수 입력값입니다.")
  private String shopId;

  @NotBlank(message = "고객님의 아이디는 필수 입력값입니다.")
  private String customerId;

  @NotBlank(message = "배달주소는 필수 입력값입니다.")
  private String address;

  @NotBlank(message = "전화번호는 필수 입력값입니다.")
  private String phoneNumber;

  private String comment;

  private DeliveryStatus deliveryStatus;

  @NotNull(message = "주문일시는 필수 입력값입니다.")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]")
  private LocalDateTime orderTime;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]")
  private LocalDateTime pickupTime;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]")
  private LocalDateTime finishTime;
}
