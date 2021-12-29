package com.inbobwetrust.relay.domain;

import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@ToString
public class RelayRequest {
  @Id private String id;

  private ReceiverType receiverType;

  private String receiverId;

  private Delivery delivery;

  public RelayRequest(ReceiverType receiverType, String receiverId, Delivery delivery) {
    this.receiverId = receiverId;
    this.receiverType = receiverType;
    this.delivery = delivery;
  }

  public Delivery getDelivery() {
    return delivery;
  }
}
