package com.kosuri.stores.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;

@Data
public class OrderDetailsDto {
    private int lineItemId;
    private String itemName;
    private String itemCategory;
    private int orderQty;
    private String deliveryQty;
    private BigDecimal mrp;
    private float discount;
    private float gst;
    private float otherOffer;
    private String invoiceId;
    private BigDecimal totalAmount;
    private String brandName;
    private String manufactureName;
    private String batchName;
    private LocalDate expiryDate;
    private String itemCode;

}
