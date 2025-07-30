package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import com.kosuri.stores.model.enums.OrderStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer_order_details")
@Getter
@Setter
public class CustomerMembershipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "customer_order_date")
    private LocalDateTime customerOrderDate;
    
    @Column(name = "order_id")
    private String orderId;

	@Column(nullable = false, length = 100)
	private double orderAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "customer_order_Status", nullable = false)
	private OrderStatus customerOrderStatus;

	private String customerOrderId;
	
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

}

