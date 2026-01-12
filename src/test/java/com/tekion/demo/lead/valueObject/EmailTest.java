package com.tekion.demo.lead.valueObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_name@example.com",
            "123@example.com",
            "test@sub.example.com"
    })
    void shouldCreateValidEmail(String validEmail) {
        Email email = new Email(validEmail);
        assertEquals(validEmail, email.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "@example.com",
            "user@",
            "user @example.com",
            ""
    })
    void shouldRejectInvalidEmail(String invalidEmail) {
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    void shouldRejectNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    void shouldBeImmutable() {
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");
        
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}

