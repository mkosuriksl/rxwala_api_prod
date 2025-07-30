package com.kosuri.stores.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemStockResponse {
    private String storeName;
    private String location;
    private String medicineName;
    private Double mrpPack;
    private String batchNo;
    private String expiryDate;
    private String ownerContact;
    private String ownerEmail;
    private int discount;
}