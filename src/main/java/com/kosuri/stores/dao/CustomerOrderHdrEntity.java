package com.kosuri.stores.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kosuri.stores.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_order_method_header")
public class CustomerOrderHdrEntity {

    @Id
    @Column(name = "OrderId")
    private String orderId;

    @Column(name = "Order_Date")
    @CreationTimestamp
    private Date orderDate;

    @Column(name = "Status")
    private OrderStatus status;
    
    @Column(name = "orderUpdated_date")
    private Date orderUpdatedDate;
    
    @Column(name = "orderUpdated_by")
    private String orderUpdatedBy;
    
    @Column(name = "updated_date")
    private Date updatedDate;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
	@Column(name = "store_id")
	private String storeId;

	@OneToMany(mappedBy = "customerOrderOrderHdrEntity", cascade = CascadeType.ALL)
	@JsonManagedReference
	@JsonIgnore
	private List<CustomerOrderDetailsEntity> customerOrderDetailsEntities;

    @PrePersist
    private void prePersist() {

        String sequenceNumber = generateRandomSixDigitNumber();
        this.orderId = "CCNO"+ LocalDate.now().getYear()+sequenceNumber;

    }

    private static final Random random = new Random();

    private String generateRandomSixDigitNumber() {
        // Generate a random integer between 100000 and 999999
        int randomInt = random.nextInt(900000) + 100000;
        return String.valueOf(randomInt);
    }


}
