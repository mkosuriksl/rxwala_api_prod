package com.kosuri.stores.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, String> {
	Optional<List<PurchaseEntity>> findByStoreId(String storeId);

	List<PurchaseEntity> findAll(Specification<PurchaseEntity> spec);

	List<PurchaseEntity> findByBillNo(String billNo);

	Optional<PurchaseEntity> findByUserIdStoreIdItemCode(String userIdStoreIdItemCode);
	
	@Query("SELECT p FROM PurchaseEntity p WHERE p.storeId = :storeId AND p.date BETWEEN :fromDate AND :toDate")
	List<PurchaseEntity> findByStoreAndDateRange(@Param("storeId") String storeId,
	                                              @Param("fromDate") Date fromDate,
	                                              @Param("toDate") Date toDate);


	@Query("SELECT p FROM PurchaseEntity p WHERE p.storeId = :storeId")
	List<PurchaseEntity> findByStoreIdOne(@Param("storeId") String storeId);
	
	@Query("SELECT p FROM PurchaseEntity p WHERE p.date BETWEEN :from AND :to")
	List<PurchaseEntity> findByDateBetweenCustom(@Param("from") Date from, @Param("to") Date to);

	Optional<PurchaseEntity> findByBillNoLineId(String billNoLineId);

	PurchaseEntity findByUserIdStoreIdItemCodeAndBatchNo(String userIdStoreIdItemCode, String batchNo);

	Page<PurchaseEntity> findAll(Specification<PurchaseEntity> spec, Pageable pageable);



}
