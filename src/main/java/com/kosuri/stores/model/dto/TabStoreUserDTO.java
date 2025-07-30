package com.kosuri.stores.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TabStoreUserDTO {
	private String userId;
	private String username;
	private String storeUserContact;
	private String storeUserEmail;
	private LocalDateTime registrationDate;
	private String addedBy;
	private String storeAdminEmail;
	private String storeAdminContact;
	private String status;
	private String userType;
}
