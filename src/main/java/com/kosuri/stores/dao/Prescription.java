package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "prescription")
@Data 
public class Prescription {

    @Id
    @Column(name = "visit_ord_no")
    private String visitOrdNo;

    @Column(name = "medicine_name")
    private String medicineName;

    @Column(name = "morning_qty")
    private int morningQty;

    @Column(name = "afternoon_qty")
    private int afternoonQty;

    @Column(name = "night_qty")
    private int nightQty;

    @Column(name = "before_food")
    private boolean beforeFood;

    @Column(name = "after_food")
    private boolean afterFood;

    @Column(name = "visiting_date")
    private LocalDateTime visitingDate;

    @Column(name = "user_id_store_id")
    private String userIdStoreId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "store_id")
    private String storeId;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
