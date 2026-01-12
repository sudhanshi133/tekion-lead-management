package com.tekion.demo.lead;

import com.tekion.demo.lead.valueObject.Email;
import com.tekion.demo.lead.valueObject.PhoneCoordinate;
import com.tekion.demo.lead.valueObject.VehicleInterest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lead {
    private String leadId;
    private String tenantId;
    private String dealerId;
    private String siteId;
    private String firstName;
    private String lastName;

    private Email email;
    private PhoneCoordinate phone;
    private VehicleInterest vehicleInterest;
    private LeadSource source;
    private LeadState state;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}