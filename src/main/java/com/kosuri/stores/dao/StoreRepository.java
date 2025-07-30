package com.kosuri.stores.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, String> {

	Optional<List<StoreEntity>> findByOwnerEmail(String ownerEmail);

	Optional<List<StoreEntity>> findByOwnerEmailOrOwnerContact(String ownerEmail, String ownerContact);

	List<StoreEntity> findByOwnerEmailAndOwnerContact(String ownerEmail, String ownerContact);

	@Query("SELECT s FROM StoreEntity s WHERE s.registrationDate BETWEEN :fromRegDate AND :toRegDate ORDER BY s.registrationDate ASC")
	List<StoreEntity> findAllBetweenRegistrationDate(LocalDate toRegDate, LocalDate fromRegDate);

	// @Query("SELECT s FROM StoreEntity s WHERE LOWER(s.location) =
	// LOWER(:location)")
	List<StoreEntity> findByLocationContaining(@Param("location") String location);

	Optional<StoreEntity> findByPincodeAndDistrictAndStateAndLocation(String pincode, String district, String state,
			String location);

	@Query("SELECT s FROM StoreEntity s WHERE s.id = :storeId")
	Optional<StoreEntity> findById(@Param("storeId") String storeId);
	
	@Query("SELECT c FROM StoreEntity c WHERE c.id IN :storeIds")
	List<StoreEntity> findById(@Param("storeIds") List<String> storeIds);
	
	@Query("SELECT c FROM StoreEntity c WHERE c.id IN :storeId")
	List<StoreEntity> findByIds(@Param("storeId") List<String> storeId);

//	List<StoreEntity> findByLocationAndType(String location, String type);
	@Query("SELECT s FROM StoreEntity s WHERE s.location = :location AND s.type = :type")
	List<StoreEntity> findByLocationAndType(@Param("location") String location, @Param("type") String type);

	List<StoreEntity> findByOwnerEmailAndType(String ownerEmail, String type);

	List<StoreEntity> findByRegistrationDate(LocalDate registrationDate);

	Optional<StoreEntity> findByIdAndStoreBusinessType(String storeId, String businessType);

	@Query("SELECT s FROM StoreEntity s WHERE s.ownerEmail = :ownerEmail")
	Optional<StoreEntity> findByEmail(String ownerEmail);

	Optional<StoreEntity> findByLocationAndUserIdStoreId(String location,String userIdStoreId);

	List<StoreEntity> findByStoreBusinessType(String businessType);

	@Query("SELECT s FROM StoreEntity s WHERE "
			+ "( :businessType IS NULL OR s.storeBusinessType = :businessType ) AND "
			+ "( :storeName IS NULL OR s.name = :storeName ) AND " + "( :location IS NULL OR s.location = :location )")
	List<StoreEntity> findByStoreBusinessTypeOrNameOrLocation(String businessType, String storeName, String location);

	 Optional<StoreEntity> findByUserIdStoreId(String userIdStoreId);
	 
	 @Query("""
				SELECT s FROM StoreEntity s
				WHERE s.userId = :storeUser
				AND (:location IS NULL OR s.location = :location)
				AND (:storeType IS NULL OR s.type = :storeType)
				AND (:addedDate IS NULL OR s.registrationDate = :addedDate)
				AND (:mobile IS NULL OR s.ownerContact = :mobile)
				AND (:email IS NULL OR s.ownerEmail = :email)
				AND (:toRegDate IS NULL OR s.registrationDate <= :toRegDate)
				AND (:fromRegDate IS NULL OR s.registrationDate >= :fromRegDate)
				""")
		List<StoreEntity> findAllByData(String storeUser, String location, String storeType,
				LocalDate addedDate, String mobile, String email, LocalDate toRegDate, LocalDate fromRegDate);

	Optional<StoreEntity> findByType(String storeId);
	
	@Query("SELECT s FROM StoreEntity s WHERE s.type = :type")
	List<StoreEntity> findByTypes(@Param("type") String type);

	@Query("SELECT DISTINCT se.location FROM StoreEntity se WHERE se.type = :storeCategory")
	List<String> findDistinctLocationsByType(String storeCategory);
	
	@Query("SELECT DISTINCT se.location FROM StoreEntity se WHERE se.type = :storeCategory AND se.storeBusinessType = :storeBusinessType")
    List<String> findDistinctLocationsByTypeAndBusinessType(@Param("storeCategory") String storeCategory,
                                                            @Param("storeBusinessType") String storeBusinessType);

	List<StoreEntity> findDistinctByLocation(String location);
	
	@Query("SELECT s FROM StoreEntity s WHERE s.userIdStoreId = :userIdStoreId")
	List<StoreEntity> findByUserIdStoreIds(@Param("userIdStoreId") String userIdStoreId);
	
	@Query("SELECT s FROM StoreEntity s WHERE s.userIdStoreId = :userIdStoreId")
	StoreEntity findByUserIdStoreIdone(@Param("userIdStoreId") String userIdStoreId);


	List<StoreEntity> findByLocationAndStoreBusinessType(String location, String storeBusinessType);

	List<StoreEntity> findAllByName(String name);

	@Query("SELECT s FROM StoreEntity s WHERE s.id = :id")
	List<StoreEntity> findByStoreId(String id);
	
	@Query("SELECT DISTINCT s.storeBusinessType FROM StoreEntity s WHERE s.storeBusinessType IS NOT NULL")
    List<String> findDistinctStoreBusinessTypes();
	
	@Query("SELECT DISTINCT s.storeBusinessType FROM StoreEntity s WHERE s.location = :location AND s.storeBusinessType IS NOT NULL")
    List<String> findDistinctStoreBusinessTypesByLocation(String location);
	
	List<StoreEntity> findByExpiryDate(String expiryDate);

	@Query("SELECT s FROM StoreEntity s " +
		       "WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
		       "AND (:type IS NULL OR s.type = :type)")
    List<StoreEntity> findByNameContainingIgnoreCaseAndType(String name, String type);

}
