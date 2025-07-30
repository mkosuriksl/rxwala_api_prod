package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientVisitHistoryRepository extends JpaRepository<PatientVisitHistoryEntity, String> {

//	@Query("SELECT pvse FROM PatientVisitHistoryEntity pvse WHERE pvse.customerRegisterEntity.cId = :cid")
//	List<PatientVisitHistoryEntity> findByCid(String cid);
//
//	@Query("SELECT pvse FROM PatientVisitHistoryEntity pvse WHERE pvse.customerRegisterEntity.cId = :cid AND pvse.visitOrdNo = :visitOrdNo")
//	Optional<PatientVisitHistoryEntity> findByCidAndVisitOrdNo(String cid, String visitOrdNo);
	
	@Query("SELECT p FROM PatientVisitHistoryEntity p WHERE p.cId = :cId")
	List<PatientVisitHistoryEntity> findByCId(@Param("cId") String cId);
}
