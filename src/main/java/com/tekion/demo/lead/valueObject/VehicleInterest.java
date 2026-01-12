package com.tekion.demo.lead.valueObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
@Data
@AllArgsConstructor
public class VehicleInterest {

    String make;
    String model;
    Integer year;
    Double tradeInValue;
}
