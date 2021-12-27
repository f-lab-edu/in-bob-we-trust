package com.inbobwetrust.controller;

import com.inbobwetrust.domain.Delivery;
import com.inbobwetrust.domain.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

public class TestParameterGenerator {
  static List<String> riderIds = List.of("rider-12830hf121");
  static List<String> orderIds = List.of("order_12830hf121");
  static List<String> agencyIds = List.of("agency-12830hf121");
  static List<String> shopIds = List.of("shop-12830hf121");
  static List<String> customerIds = List.of("customer-12830hf121");
  static List<String> addresses = List.of("address-12830hf121");
  static List<String> phoneNumbers = List.of("phoneNumber-12830hf121");
  static List<String> comments = List.of("comment-12830hf121");
  static List<DeliveryStatus> deliveryStatuses = List.of(DeliveryStatus.values());
  static List<LocalDateTime> orderTimes = generateTimes();
  static List<LocalDateTime> pickupTimes = generateTimes();
  static List<LocalDateTime> finishTimes = generateTimes();

  private static List<LocalDateTime> generateTimes() {
    return List.of(now(), now().minusSeconds(1), now().minusMinutes(1), now().minusHours(1));
  }

  public static List<Delivery> generate() {
    List<Delivery> result = new ArrayList<>();
    for (String riderId : riderIds) {
      for (String agencyId : agencyIds) {
        for (String shopId : shopIds) {
          for (String customerId : customerIds) {
            for (String address : addresses) {
              for (String phoneNumber : phoneNumbers) {
                for (String comment : comments) {
                  for (DeliveryStatus status : deliveryStatuses) {
                    for (LocalDateTime orderTime : orderTimes) {
                      for (LocalDateTime pickupTime : pickupTimes) {
                        for (LocalDateTime finishTime : finishTimes) {
                          for (String orderId : orderIds) {
                            result.add(
                                Delivery.builder()
                                    .orderId(orderId)
                                    .riderId(riderId)
                                    .agencyId(agencyId)
                                    .shopId(shopId)
                                    .customerId(customerId)
                                    .address(address)
                                    .phoneNumber(phoneNumber)
                                    .comment(comment)
                                    .deliveryStatus(status)
                                    .orderTime(orderTime)
                                    .pickupTime(pickupTime)
                                    .finishTime(finishTime)
                                    .build());
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return result;
  }
}
