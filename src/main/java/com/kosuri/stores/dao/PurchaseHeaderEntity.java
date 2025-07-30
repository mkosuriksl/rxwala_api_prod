package com.kosuri.stores.dao;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "pharma_purchase_header")
public class PurchaseHeaderEntity {

    @Id
    @Column(name = "Bill_No", nullable = false, unique = true)
    private String billNo;

    @Column(name = "Date")
    private Date date;

    @Column(name = "Supp_Code")
    private String suppCode;

    @Column(name = "Supp_Name")
    private String suppName;

    @Column(name = "Store_ID")
    private String storeId;
    
    @Column(name = "UserIdStoreId")
    private String userIdStoreId;
    
    @Column(name = "Taxable_Amt")
    private Double taxableAmt;

    @Column(name = "Cgst_Amt")
    private Double cGSTAmt;

    @Column(name = "Sgst_Amt")
    private Double sGSTAmt;

    @Column(name = "Igst_Amt")
    private Double iGSTAmt;

    @Column(name = "Cess_Amt")
    private Double cessAmt;

    @Column(name = "Total")
    private Double total;

    
}

