package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kosuri.stores.model.request.LoginUserRequest;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, String> {

	@Query("SELECT ae FROM AdminEntity ae WHERE (COALESCE(:emailId, '') = '' OR ae.emailId = :emailId) OR (COALESCE(:phone, '') = '' OR ae.mobileNo = :phone)")
	Optional<AdminEntity> findByPhoneOrEmail(String emailId, String phone);

	Optional<AdminEntity> findByEmailId(String adminEamil);

	Optional<AdminEntity> findByEmailIdOrMobileNo(String email, String phoneNumber);

	Optional<AdminEntity> findByMobileNo(String username);
}
