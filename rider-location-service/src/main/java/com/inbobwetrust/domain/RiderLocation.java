package com.inbobwetrust.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderLocation {
  private String id;
  private Float latitude;
  private Float longitude;
}
