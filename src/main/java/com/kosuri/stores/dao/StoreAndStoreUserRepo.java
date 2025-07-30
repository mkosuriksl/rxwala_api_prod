package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface StoreAndStoreUserRepo extends JpaRepository<StoreAndStoreUserEntity, String> {

	 Optional<StoreAndStoreUserEntity> findFirstBySuUserId(String suUserId);
	
	@Query("SELECT s FROM StoreAndStoreUserEntity s WHERE s.updatedBy = :userId")
	List<StoreAndStoreUserEntity> findByUpdatedBy(@Param("userId") String userId);

	List<StoreAndStoreUserEntity> findBySuUserIdAndStoreId(String suUserId, String storeId);
	
	@Query("SELECT s FROM StoreAndStoreUserEntity s WHERE s.userIdstoreId = :userIdstoreId")
	List<StoreAndStoreUserEntity> findByUserIdstoreId(@Param("userIdstoreId") String userIdstoreId);

	List<StoreAndStoreUserEntity> findBySuUserId(String suUserId);

	List<StoreAndStoreUserEntity> findByStoreId(String storeId);

}
