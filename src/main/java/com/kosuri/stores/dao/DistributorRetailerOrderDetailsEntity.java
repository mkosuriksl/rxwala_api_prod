package com.kosuri.stores.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "distributor_retailer_orders_details_two")
public class DistributorRetailerOrderDetailsEntity {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private int id;
	@Id
	@Column(name = "Orderline_Id")
	private String orderlineId;

	@Column(name = "Retailer_ID")
	private String retailerId;

	@Column(name = "Item_Name")
	private String itemName;

	@Column(name = "Item_Category")
	private String itemCategory;

	@Column(name = "Brand_Name")
	private String brandName;

	@Column(name = "Manufacturer_Name")
	private String manufacturerName;

	@Column(name = "Ord_Qty")
	private Integer orderQuantity;

	@Column(name = "Delivery_Qty")
	private Integer deliveryQuantity;

	@Column(name = "Mrp")
	private Double mrp;

	@Column(name = "Discount")
	private Integer discount;

	@Column(name = "Cash_Discount")
	private String cashDiscount;

	@Column(name = "Offer")
	private String offer;

	@Column(name = "Sgst")
	private Integer sgst;

	@Column(name = "Cgst")
	private Integer cgst;

	@Column(name = "Sgst_Amt")
	private Double sgstAmount;

	@Column(name = "Cgst_Amt")
	private Double cgstAmount;

	@Column(name = "Total")
	private Double total;

	@Column(name = "Status")
	private String status;

	@Column(name = "DistributorID")
	private String distributorId;

	@Column(name = "Batch_Number")
	private String batchNumber;

	@Column(name = "Exp_Date")
	private LocalDate expiryDate;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "orderId", referencedColumnName = "orderId")
	private DistributorRetailerOrderHdrEntity distributorRetailerOrderHdr;
	
	@Column(name = "Item_Code")
	private String itemCode;
	
	private @Column(name = "UserIdStoreId_ItemCode") String userIdStoreIdItemCode;	
	
    @Column(name = "store_id")
    private String storeId;

    @Column(name = "invoice_no")
    private String invoiceNo;
}
