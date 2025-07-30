package com.kosuri.stores.dao;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "item_code_master")
public class ItemCodeMaster {

    @Id
    @Column(name = "UserIdStoreId_ItemCode",unique = true, nullable = false)
    private String userIdStoreIdItemCode;

    @Column(name = "Store_ID")
    private String storeId;

    @Column(name = "Item_Code")
    private String itemCode;

    @Column(name = "Item_Name")
    private String itemName;

    @Column(name = "Item_Category")
    private String itemCategory;

    @Column(name = "Item_SubCategory")
    private String itemSubCategory;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "brand")
    private String brand;

    @Column(name = "gst")
    private Integer gst;
    
    @Column(name = "hsn_group")
    private String hsnGroup;
    

    @Column(name = "Updated_By")
    private String updatedBy;

    @Column(name = "Updated_Date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @Column(name = "User_ID")
    private String userId;

    @Column(name = "UserIdStoreId")
    private String userIdStoreId;

    @PrePersist
    private void prePersist() {
        if (userId == null || storeId == null || itemCode == null) {
            throw new IllegalStateException("userId, storeId, and itemCode must be set before saving.");
        }

        if (userIdStoreIdItemCode == null || userIdStoreIdItemCode.isBlank()) {
            this.userIdStoreIdItemCode = userId + "_" + storeId + "_" + itemCode;
        }
    }

}
