package com.kosuri.stores.dao;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "admin_user_login")
public class AdminEntity {

	@Id
	@Column(name = "email_id", unique = true, nullable = false)
	private String emailId;

	@Column(name = "mobile_no", unique = true, nullable = false)
	private String mobileNo;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "pwd", nullable = false, length = 250)
	private String pwd;

	@Column(name = "work_location")
	private String workLocation;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "user_role", nullable = false)
	private String userRole;
	
	
}
