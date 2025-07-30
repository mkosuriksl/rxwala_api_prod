package com.kosuri.stores.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_to_retailer_order_detail")
public class CustomerRetailerOrderDetailsEntity {
    @Id
    private String lineItemId;
    private String itemName;
    private String itemCategory;
    private int orderQty;
    private int deliveryQty;
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
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="orderId",referencedColumnName = "orderId")
    private CustomerRetailerOrderHdrEntity customerRetailerOrderHdr;



}
