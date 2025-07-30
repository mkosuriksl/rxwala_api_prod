package com.kosuri.stores.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PatientPharmacyRepository extends JpaRepository<PatientPharmacy, String> {

	@Query("SELECT c FROM PatientPharmacy c WHERE c.visitOrdNo IN :visitOrdNos")
	List<PatientPharmacy> findByVisitOrdNoIn(List<String> visitOrdNos);

}
