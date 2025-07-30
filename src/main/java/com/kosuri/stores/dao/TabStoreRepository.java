package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TabStoreRepository extends JpaRepository<TabStoreUserEntity, String> {

	Optional<TabStoreUserEntity> findByStoreUserEmailOrStoreUserContact(String storeUserEmail, String storeUserContact);

	@Query("SELECT u FROM TabStoreUserEntity u WHERE u.storeUserEmail = :email OR u.storeUserContact = :phone "
			+ "OR u.storeAdminEmail = :email OR u.storeAdminContact = :phone")
	Optional<TabStoreUserEntity> findByStoreUserEmailOrStoreUserContactOrStoreAdminEmailOrStoreAdminContact(
			String email, String phone);
	
	@Query("SELECT u FROM TabStoreUserEntity u WHERE u.storeUserEmail = :username OR u.storeUserContact = :username ")
	Optional<TabStoreUserEntity> findByStoreUserEmailOrStoreUserContact(
			String username);

	Optional<TabStoreUserEntity> findByStoreUserEmail(String email);

	Optional<TabStoreUserEntity> findByStoreUserContact(String phoneNumber);

	Optional<TabStoreUserEntity> findByUsername(String userName);

	TabStoreUserEntity findByStoreUserEmailAndStoreUserContact(String emailAddress, String userContactNumber);

	Optional<TabStoreUserEntity> findByUserId(String userId);
	
	@Query("SELECT t FROM TabStoreUserEntity t WHERE t.userId = :userId")
	List<TabStoreUserEntity> findByUserIdOne(@Param("userId") String userId);

	List<TabStoreUserEntity> findByUserIdAndStoreUserContact(String userId, String storeUserContact);

	List<TabStoreUserEntity> findByUserIdAndStoreUserEmail(String userId, String storeUserEmail);
	
	@Query("SELECT t FROM TabStoreUserEntity t WHERE t.userId = :userId")
	List<TabStoreUserEntity> findByUserIds(@Param("userId") String userId);

	@Query("SELECT t FROM TabStoreUserEntity t WHERE t.storeUserContact = :storeUserContact")
	List<TabStoreUserEntity> findByStoreUserContacts(@Param("storeUserContact") String storeUserContact);

	@Query("SELECT t FROM TabStoreUserEntity t WHERE t.storeUserEmail = :storeUserEmail")
	List<TabStoreUserEntity> findByStoreUserEmails(@Param("storeUserEmail") String storeUserEmail);
}
