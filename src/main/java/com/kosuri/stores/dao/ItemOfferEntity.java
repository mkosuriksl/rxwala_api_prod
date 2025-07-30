package com.kosuri.stores.dao;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "item_pricing")
@Data
public class ItemOfferEntity {

	@Id
	@Column(name = "userIdStoreId_ItemCode", nullable = false)
	private String userIdStoreIdItemCode;

    @Column(name = "batch_no", nullable = false)
    private String batchNumber;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "offer_qty")
    private Double offerQty;

    @Column(name = "min_order_qty")
    private Double minOrderQty;

    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "store_id")
    private String storeId;
    
    @Column(name = "userIdStoreId")
    private String userIdStoreId;

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
		
}
