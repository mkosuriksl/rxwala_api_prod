package com.kosuri.stores.model.dto;

import java.time.LocalDateTime;

import com.kosuri.stores.dao.DiagnosticServicesEntity;

import lombok.Data;

@Data
public class DiagnosticServiceDto {
    private String userServiceId;
    private String serviceId;
    private String serviceName;
    private String price;
    private String description;
    private String userId;
    private String storeId;
    private String serviceCategory;
    private String updatedBy;
    private String status;
    private LocalDateTime amountUpdatedDate;
    private LocalDateTime statusUpdatedDate;

    public DiagnosticServiceDto(DiagnosticServicesEntity service) {
        this.userServiceId = service.getUserServiceId();
        this.serviceId = service.getServiceId();
        this.serviceName = service.getServiceName();
        this.price = service.getPrice();
        this.description = service.getDescription();
        this.userId = service.getUserId();
        this.storeId = service.getStoreId();
        this.serviceCategory = service.getServiceCategory();
        this.updatedBy = service.getUpdatedBy();
        this.status = service.getStatus();
        this.amountUpdatedDate = service.getAmountUpdatedDate();
        this.statusUpdatedDate = service.getStatusUpdatedDate();
    }
}

