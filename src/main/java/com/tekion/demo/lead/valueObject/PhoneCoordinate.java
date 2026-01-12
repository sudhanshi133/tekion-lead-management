package com.tekion.demo.lead.valueObject;

import lombok.Value;

@Value
public class PhoneCoordinate {
    String countryCode;
    String number;

    public PhoneCoordinate(String countryCode, String number) {
        if (countryCode == null || number == null || number.length() < 8) {
            throw new IllegalArgumentException(
                    "Invalid phone number: " + countryCode + " " + number);
        }
        this.countryCode = countryCode;
        this.number = number;
    }
}
