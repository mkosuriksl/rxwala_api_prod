package com.kosuri.stores.model.dto;

import com.kosuri.stores.model.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequest {

    private String serviceID;
    private Double amount;
    private Double discount;
    private String serviceName;
    private Status status;
}
