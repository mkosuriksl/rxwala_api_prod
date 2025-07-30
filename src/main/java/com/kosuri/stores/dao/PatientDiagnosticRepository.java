package com.kosuri.stores.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PatientDiagnosticRepository extends JpaRepository<PatientDiagnostic, String> {

	@Query("SELECT c FROM PatientDiagnostic c WHERE c.visitOrdNo IN :visitOrdNos")
	List<PatientDiagnostic> findByVisitOrdNoIn(List<String> visitOrdNos);

}
