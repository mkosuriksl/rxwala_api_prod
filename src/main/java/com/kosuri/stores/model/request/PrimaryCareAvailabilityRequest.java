package com.kosuri.stores.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrimaryCareAvailabilityRequest {

    private String providerId;
    private String primaryCareAvailLoc;
    private String updatedBy;
    private String availability;
}
