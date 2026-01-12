package com.tekion.demo.lead.valueObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PhoneCoordinateTest {

    @ParameterizedTest
    @CsvSource({
            "+1, 1234567890",
            "+44, 12345678",
            "+91, 9876543210",
            "+1, 123456789012345"
    })
    void shouldCreateValidPhoneCoordinate(String countryCode, String number) {
        PhoneCoordinate phone = new PhoneCoordinate(countryCode, number);
        
        assertEquals(countryCode, phone.getCountryCode());
        assertEquals(number, phone.getNumber());
    }

    @Test
    void shouldRejectNullCountryCode() {
        assertThrows(IllegalArgumentException.class, 
                () -> new PhoneCoordinate(null, "1234567890"));
    }

    @Test
    void shouldRejectNullNumber() {
        assertThrows(IllegalArgumentException.class, 
                () -> new PhoneCoordinate("+1", null));
    }

    @Test
    void shouldRejectShortNumber() {
        assertThrows(IllegalArgumentException.class, 
                () -> new PhoneCoordinate("+1", "1234567"));
    }

    @Test
    void shouldRejectEmptyNumber() {
        assertThrows(IllegalArgumentException.class, 
                () -> new PhoneCoordinate("+1", ""));
    }

    @Test
    void shouldBeImmutable() {
        PhoneCoordinate phone1 = new PhoneCoordinate("+1", "1234567890");
        PhoneCoordinate phone2 = new PhoneCoordinate("+1", "1234567890");
        
        assertEquals(phone1, phone2);
        assertEquals(phone1.hashCode(), phone2.hashCode());
    }
}

