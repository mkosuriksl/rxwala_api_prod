package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosuri.stores.model.dto.ItemCodeSearchDTO;

public interface ItemCodeMasterRepository extends JpaRepository<ItemCodeMaster, String> {

	Optional<ItemCodeMaster> findByUserIdStoreIdItemCode(String userIdStoreIdItemCode);
	
	@Query("SELECT s.userIdStoreIdItemCode AS userIdStoreIdItemCode, s.storeId AS storeId, s.itemCode AS itemCode, s.itemName AS itemName, s.itemCategory AS itemCategory, s.itemSubCategory AS itemSubCategory, s.manufacturer AS manufacturer, s.brand AS brand, s.gst AS gst,s.hsnGroup AS hsnGroup ,s.userId AS userId, s.userIdStoreId AS userIdStoreId " +
		       "FROM ItemCodeMaster s " +
		       "WHERE LOWER(s.itemCode) LIKE LOWER(CONCAT('%', :itemCode, '%'))")
		List<ItemCodeSearchDTO> searchByItemCode(@Param("itemCode") String itemCode);

	@Query("SELECT s.userIdStoreIdItemCode AS userIdStoreIdItemCode, s.storeId AS storeId, s.itemName AS itemName, s.itemCode AS itemCode, s.itemCategory AS itemCategory, s.itemSubCategory AS itemSubCategory, s.manufacturer AS manufacturer, s.brand AS brand, s.gst AS gst, s.hsnGroup AS hsnGroup ,s.userId AS userId, s.userIdStoreId AS userIdStoreId " +
		       "FROM ItemCodeMaster s " +
		       "WHERE LOWER(s.itemName) LIKE LOWER(CONCAT('%', :itemName, '%'))")
		List<ItemCodeSearchDTO> searchByItemName(@Param("itemName") String itemName);
	
	@Query("SELECT MAX(i.itemCode) FROM ItemCodeMaster i WHERE i.userIdStoreIdItemCode LIKE CONCAT(:prefix, '%')")
	String findMaxItemCodeByPrefix(@Param("prefix") String prefix);
	
	Optional<List<ItemCodeMaster>> findByStoreId(String storeId);


}
