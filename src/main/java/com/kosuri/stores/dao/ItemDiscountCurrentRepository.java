package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDiscountCurrentRepository extends JpaRepository<ItemDiscountCurrent, String> {

	 List<ItemDiscountCurrent> findByUserIdStoreId(String userIdStoreId);

	 
	 @Query("SELECT idc FROM ItemDiscountCurrent idc WHERE idc.userIdStoreId = :userIdStoreId")
	List<ItemDiscountCurrent> findByUserIdStoreIdIns(String userIdStoreId);
	 
	 @Query("SELECT idc FROM ItemDiscountCurrent idc WHERE idc.userIdStoreId = :userIdStoreId")
	 List<ItemDiscountCurrent> findByUserIdStoreIdIn(String userIdStoreId);


	Optional<ItemDiscountCurrent> findByItemCode(String itemCode);


	List<ItemDiscountCurrent> findByUserIdStoreIdItemCodeIn(List<String> userIdStoreIdItemCodes);
	 
}
