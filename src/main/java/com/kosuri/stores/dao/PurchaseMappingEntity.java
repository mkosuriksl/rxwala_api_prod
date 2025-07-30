package com.kosuri.stores.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "purchase_mapping")
@Data
public class PurchaseMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userIdStoreIdItemCode;
    private Integer gstCode;
    private String itemCode;
    private String itemCat;
    private String storeId;
    private String userId;
}

