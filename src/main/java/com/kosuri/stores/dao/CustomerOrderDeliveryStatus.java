package com.kosuri.stores.dao;

import java.util.Date;

import org.hibernate.annotations.UpdateTimestamp;

import com.kosuri.stores.model.enums.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_order_delivery_status")
public class CustomerOrderDeliveryStatus {

    @Id
    private String orderId;

    private String customerId;

    @UpdateTimestamp
    private Date updatedDate;
    
    private String updatedBy;
    
    @UpdateTimestamp
    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "delivery_location")
    private String deliveryLocation;
    
    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus paymentStatus;
    
    @Column(name = "delivery_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus delivaryStatus;

}
