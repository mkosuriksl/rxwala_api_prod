package com.kosuri.stores.dao;

import com.kosuri.stores.utils.RandomUtils;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "membersip_hdr")
public class MembershipHdrEntity {
	public MembershipHdrEntity() {
		this.orderId = new RandomUtils().generateRandomNumber();
	}

	@Id
	@Nonnull
	private @Column(name = "order_id") String orderId;
	private @Column(name = "order_date") LocalDateTime orderDate;
	private @Column(name = "order_amount") Integer OrderAmount;
	private @Column(name = "user_id") String userId;
	private @Column(name = "payment_method") String paymentMethod;
	private @Column(name = "transaction_number") String transactionNumber;
	private @Column(name = "transaction_Date") LocalDateTime transactionDate;
	private @Column(name = "is_verified") Boolean isVerified;

}