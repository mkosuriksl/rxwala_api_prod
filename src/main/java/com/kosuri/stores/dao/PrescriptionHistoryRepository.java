package com.kosuri.stores.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrescriptionHistoryRepository extends JpaRepository<PrescriptionHistory, String> {
	List<PrescriptionHistory> findByVisitOrdNo(String visitOrdNo);
    long countByVisitOrdNo(String visitOrdNo);
    
    @Query("SELECT DISTINCT p.visitOrdNo FROM PrescriptionHistory p " +
    	       "WHERE (:visitOrdNo IS NULL OR p.visitOrdNo = :visitOrdNo) " +
    	       "AND (:medicineName IS NULL OR LOWER(p.medicineName) LIKE LOWER(CONCAT('%', :medicineName, '%'))) " +
    	       "AND (:userId IS NULL OR p.userId = :userId) " +
    	       "AND (:userIdStoreId IS NULL OR p.userIdStoreId = :userIdStoreId)")
    	Page<String> findByVisitOrdNo(@Param("visitOrdNo") String visitOrdNo,
    	                                    @Param("medicineName") String medicineName,
    	                                    @Param("userId") String userId,
    	                                    @Param("userIdStoreId") String userIdStoreId,
    	                                    Pageable pageable);
}

