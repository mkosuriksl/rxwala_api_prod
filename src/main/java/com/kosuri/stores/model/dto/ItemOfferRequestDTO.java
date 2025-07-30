package com.kosuri.stores.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemOfferRequestDTO {
    private String storeId;
    private String userId;
    private String userIdStoreId;
    private List<ItemOfferDTO> itemOffers;
}
