package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import com.kosuri.stores.utils.RandomUtils;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "membersip_details")
public class MembershipDetailsEntity {
	public MembershipDetailsEntity() {
		this.lineitemId = new RandomUtils().generateRandomNumber();
	}

	@Id
	@Nonnull
	private @Column(name = "lineitem_id") String lineitemId;
	@ManyToOne
	@JoinColumn(name = "order_id")
	private MembershipHdrEntity orderId;
	private @Column(name = "store_id") String storeId;
	private @Column(name = "plan_id") String planId;
	private @Column(name = "no_of_days") String noOfDays;
	private @Column(name = "amount") Integer amount;
	private @Column(name = "expiry_date") LocalDateTime expiryDate;
	private @Column(name = "user_id_store_id") String userIdStoreId;

}