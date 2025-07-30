package com.kosuri.stores.dao;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "item_offer_history")
@Data
public class ItemOfferHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userIdStoreIdItemCode;

    private String batchNumber;

    private Double discount;

    private Double offerQty;

    private Double minOrderQty;

    private String updatedBy;

    private Date updatedDate;
}

