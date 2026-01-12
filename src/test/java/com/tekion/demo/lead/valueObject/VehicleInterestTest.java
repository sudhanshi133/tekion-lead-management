package com.tekion.demo.lead.valueObject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleInterestTest {

    @Test
    void shouldCreateVehicleInterest() {
        VehicleInterest vehicle = new VehicleInterest("Toyota", "Camry", 2020, 5000.0);
        
        assertEquals("Toyota", vehicle.getMake());
        assertEquals("Camry", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals(5000.0, vehicle.getTradeInValue());
    }

    @Test
    void shouldCreateVehicleInterestWithNullTradeInValue() {
        VehicleInterest vehicle = new VehicleInterest("Honda", "Accord", 2019, null);
        
        assertEquals("Honda", vehicle.getMake());
        assertEquals("Accord", vehicle.getModel());
        assertEquals(2019, vehicle.getYear());
        assertNull(vehicle.getTradeInValue());
    }

    @Test
    void shouldCreateVehicleInterestWithBuilder() {
        VehicleInterest vehicle = VehicleInterest.builder()
                .make("Ford")
                .model("F-150")
                .year(2021)
                .tradeInValue(15000.0)
                .build();
        
        assertEquals("Ford", vehicle.getMake());
        assertEquals("F-150", vehicle.getModel());
        assertEquals(2021, vehicle.getYear());
        assertEquals(15000.0, vehicle.getTradeInValue());
    }

    @Test
    void shouldBeImmutable() {
        VehicleInterest vehicle1 = new VehicleInterest("Toyota", "Camry", 2020, 5000.0);
        VehicleInterest vehicle2 = new VehicleInterest("Toyota", "Camry", 2020, 5000.0);
        
        assertEquals(vehicle1, vehicle2);
        assertEquals(vehicle1.hashCode(), vehicle2.hashCode());
    }

    @Test
    void shouldHandleZeroTradeInValue() {
        VehicleInterest vehicle = new VehicleInterest("Nissan", "Altima", 2018, 0.0);
        
        assertEquals(0.0, vehicle.getTradeInValue());
    }
}

