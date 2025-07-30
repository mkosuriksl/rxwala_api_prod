package com.kosuri.stores.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceResponseDto {
    private String packageId;
    private String packageIdLineId;
    private String serviceId;
    private double amount;
    private int discount;
    private String serviceName;
    private String updatedBy;
}
