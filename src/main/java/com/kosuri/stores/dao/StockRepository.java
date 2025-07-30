package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, String> {

	StockEntity findByMfNameAndItemNameAndBatchAndStoreIdAndSupplierName(String mFName, String itemName, String batch,
			String storeId, String supplierName);

	List<StockEntity> findByItemNameContainingAndStoreIdAndItemCategoryAndBalQuantityGreaterThan(String itemName,
			String storeId, String category, Double balQuantity);

	List<StockEntity> findByItemNameContainingAndStoreIdAndBalQuantityGreaterThan(String itemName, String storeId,
			Double balQuantity);

	Optional<List<StockEntity>> findByStoreId(String storeId);

	List<StockEntity> findFirstByItemNameContainingAndStoreIdAndBalQuantityGreaterThan(String itemName, String storeId,
			Double balQuantity);

	List<StockEntity> findAllByUserIdStoreIdAndItemNameAndMfName(String userIdStoreId, String itemName, String manufactureName);

	@Query("SELECT s FROM StockEntity s WHERE s.storeId = :storeId "
			+ "AND (:itemCategory IS NULL OR s.itemCategory = :itemCategory) "
			+ "AND (:itemName IS NULL OR s.itemName = :itemName)")
	List<StockEntity> findAllByStoreIdAndItemCategoryOrItemName(String storeId, String itemCategory, String itemName);

	List<StockEntity> findFirstByItemNameContainingAndStoreId(String medicine, String id);

	@Query("SELECT s FROM StockEntity s WHERE LOWER(s.itemName) LIKE LOWER(CONCAT('%', :itemName, '%'))")
	List<StockEntity> findByItemName(@Param("itemName") String itemName);
	
//	@Query("SELECT s FROM StockEntity s " +
//		       "WHERE LOWER(s.itemName) LIKE LOWER(CONCAT('%', :itemName, '%')) " +
//		       "OR LOWER(s.mfName) = LOWER(:mfName)")
//		List<StockEntity> findByItemNameLikeOrMfNameExact(@Param("itemName") String itemName,
//		                                                  @Param("mfName") String mfName);
	@Query("SELECT s FROM StockEntity s " +
		       "WHERE LOWER(s.itemName) LIKE LOWER(CONCAT('%', :itemName, '%')) " +
		       "OR LOWER(s.mfName) LIKE LOWER(CONCAT('%', :mfName, '%'))")
		List<StockEntity> findByItemNameLikeOrMfNameExact(@Param("itemName") String itemName,
		                                                  @Param("mfName") String mfName);



	@Query("SELECT DISTINCT s FROM StockEntity s " + "JOIN ItemDiscountCurrent idc ON s.itemCode = idc.itemCode "
			+ "JOIN StoreEntity se ON idc.userIdStoreId = se.userIdStoreId " + "WHERE se.location = :location "
			+ "AND s.itemName = :itemName")
	List<StockEntity> findStockByLocationAndItemName(@Param("location") String location,
			@Param("itemName") String itemName);

	List<StockEntity> findByItemCodeAndItemName(String itemCode, String itemName);

	  @Query("SELECT s FROM StockEntity s JOIN StoreEntity st ON s.storeId = st.id " +
	           "WHERE s.itemName = :itemName AND st.location = :location")
	    List<StockEntity> findByItemNameAndLocation(@Param("itemName") String itemName, 
	                                                @Param("location") String location);
	  
	  @Query("SELECT s FROM StockEntity s WHERE s.storeId = :storeId")
	  List<StockEntity> findByStoreIdOne(@Param("storeId") String storeId);
	  
	  @Query("SELECT DISTINCT s.itemName FROM StockEntity s")
		List<String> findItemName();

	StockEntity findByItemCode(String itemCode);
	StockEntity findByItemCodeAndBatch(String itemCode,String batch);
	
	StockEntity findByUserIdStoreIdItemCodeAndBatch(String userIdStoreIdItemCode,String batch);

	StockEntity findByUserIdStoreIdItemCode(String userIdStoreIdItemCode);
	
	@Query("SELECT s FROM StockEntity s WHERE s.userIdStoreIdItemCode = :userIdStoreIdItemCode")
	List<StockEntity> findAllByUserIdStoreIdItemCode(@Param("userIdStoreIdItemCode") String userIdStoreIdItemCode);

}
