package com.kosuri.stores.dao;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tab_store_user_login")
public class TabStoreUserEntity {
	private @Column(name = "store_category") String type;

	@Id
	@Nonnull
	private @Column(name = "user_id") String userId;
	private @Column(name = "store_user_full_name") String username;

	@Nonnull
	private @Column(name = "store_user_phonenumber", unique = true) String storeUserContact;
	@Nonnull
	private @Column(name = "store_user_emailid", unique = true) String storeUserEmail;

	@Column(name = "registraion_date")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime registrationDate;
	private @Column(name = "addedby") String addedBy;
	private @Column(name = "store_admin_email") String storeAdminEmail;
	private @Column(name = "store_admin_mobile") String storeAdminContact;
	private @Column(name = "status") String status;
	private @Column(name = "password") String password;
	private @Column(name = "user_type") String userType;

}
