package com.kosuri.stores.model.dto;

import lombok.Data;

@Data
public class ItemOfferDTO {
    private String userIdStoreId_itemCode;
    private String batchNumber;
    private Double discount;
    private Double offerQty;
    private Double minOrderQty;
}
