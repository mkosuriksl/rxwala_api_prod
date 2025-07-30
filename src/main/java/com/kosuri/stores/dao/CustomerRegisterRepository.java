package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRegisterRepository extends JpaRepository<CustomerRegisterEntity, Long> {
	boolean existsByEmail(String email);

	Optional<CustomerRegisterEntity> findByEmail(String email);

	List<CustomerRegisterEntity> findByLocation(String location);

	List<CustomerRegisterEntity> findByPhoneNumberOrEmail(String customerNumber, String customerEmail);

	Optional<CustomerRegisterEntity> findByPhoneNumber(String customerNumber);

	@Query("SELECT c FROM CustomerRegisterEntity c WHERE c.cId = :cid")
	Optional<CustomerRegisterEntity> findByCid(String cid);

	@Query("SELECT c FROM CustomerRegisterEntity c WHERE c.cId = :cid OR c.email = :email OR c.phoneNumber = :phoneNumber")
	CustomerRegisterEntity findByCidOrEmailOrPhoneNumber(@Param("cid") String cid, @Param("email") String email,
			@Param("phoneNumber") String phoneNumber);

	@Query("SELECT c FROM CustomerRegisterEntity c WHERE c.email = :email OR c.phoneNumber = :phoneNumber")
	Optional<CustomerRegisterEntity> findByEmailOrPhoneNumber(String email, String phoneNumber);

	@Query("SELECT u FROM CustomerRegisterEntity u WHERE u.email = :username OR u.phoneNumber = :username ")
	Optional<CustomerRegisterEntity> findByEmailOrPhoneNumber(String username);

	@Query("SELECT c FROM CustomerRegisterEntity c WHERE c.cId = :customerId")
	CustomerRegisterEntity findByCId(@Param("customerId") String customerId);

	@Query("SELECT c FROM CustomerRegisterEntity c WHERE c.cId IN :cIds")
	List<CustomerRegisterEntity> findByCIdIn(@Param("cIds") List<String> cIds);
}
