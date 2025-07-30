package com.kosuri.stores.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.Random;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_register_three")
public class CustomerRegisterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String cId;
	private String name;
	private String email;
	private String phoneNumber;
	private String emailOtp;
	private String userType;
	private String emailVerify;
	private String mobileOtp;
	private String mobileVerify;
	private String customerStatus;
	private String password;
	private String location;
	@CreationTimestamp
	private Date registeredDate;
	@UpdateTimestamp
	private Date updatedDate;
	private String address;
	
	private String registerMode;
	private String updatedBy;

	@PrePersist
	private void prePersist() {
		// Generate the sequence number
		String sequenceNumber = generateRandomTwelveDigitNumber();

		this.cId = "cu" + sequenceNumber;
	}

	private static final Random random = new Random();

	private String generateRandomTwelveDigitNumber() {
		// Generate a random integer between 100000 and 999999
		long randomLong = random.nextLong(900000000000L) + 100000000000L;
		return String.valueOf(randomLong);
	}
}
