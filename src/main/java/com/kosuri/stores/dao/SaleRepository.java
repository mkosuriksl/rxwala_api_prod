package com.kosuri.stores.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, String> {
	Optional<List<SaleEntity>> findByStoreId(String storeId);

	@Query("Select sum(s.saleValue) from SaleEntity s where s.custName = ?1 and s.date > ?2")
	Double findTotalSalesForCustomerAfterDate(String customerName, Date date);

	@Query("Select sum(s.saleValue),s.mobile,s.custName from SaleEntity s where (s.mobile = ?1 or s.custName = ?2) and s.storeId = ?4 and s.date > ?3 group by s.mobile")
	List<Object[]> findTotalSalesForCustomerPhoneOrNameAndStoreIdAfterDate(String mobile, String customerName,
			Date date, String storeId);

	@Query("Select sum(s.saleValue),s.mobile,s.custName from SaleEntity s where (s.mobile = ?1 and s.custName = ?2) and s.storeId = ?4 and s.date > ?3 group by s.mobile")
	List<Object[]> findTotalSalesForCustomerPhoneAndNameAndStoreIdAfterDate(String mobile, String customerName,
			Date date, String storeId);
	
	List<SaleEntity> findAll(Specification<SaleEntity> spec);
	
	List<SaleEntity> findByDocNumber(String docNumber);

	Optional<SaleEntity> findByUserIdStoreIdItemCode(String userIdStoreIdItemCode);
	
	@Query("SELECT s FROM SaleEntity s WHERE s.storeId = :storeId AND s.date BETWEEN :fromDate AND :toDate")
	List<SaleEntity> findByStoreAndDateRange(@Param("storeId") String storeId,
	                                              @Param("fromDate") Date fromDate,
	                                              @Param("toDate") Date toDate);
	
	@Query("SELECT s FROM SaleEntity s WHERE s.storeId = :storeId")
	List<SaleEntity> findByStoreIdOne(@Param("storeId") String storeId);
	
	@Query("SELECT s FROM SaleEntity s WHERE s.date BETWEEN :from AND :to")
	List<SaleEntity> findByDateBetweenCustom(@Param("from") Date from, @Param("to") Date to);
	
	@Query("SELECT s FROM SaleEntity s WHERE s.userIdStoreIdItemCode = :userIdStoreIdItemCode")
	SaleEntity findByUserIdStoreIdItemCodeOne(@Param("userIdStoreIdItemCode") String userIdStoreIdItemCode);

	Optional<SaleEntity> findByDocNumberLineId(String docNumberLineId);

	Page<SaleEntity> findAll(Specification<SaleEntity> spec, Pageable pageable);

}
