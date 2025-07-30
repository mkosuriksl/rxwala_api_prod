package com.kosuri.stores.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OrderUpdatedDto {

    private long id;

    private String orderId;

    private String orderStatus;

    private Date OrderUpdatedDate;

    private String updatedBy;

    private String deliveryMethod;

    private String paymentStatus;

}
