package com.kosuri.stores.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosticServicePackageLineRepository extends JpaRepository<DiagnosticServicePackageLine, String> {

	@Query("SELECT d FROM DiagnosticServicePackageLine d WHERE (:storeId IS NULL OR d.storeId = :storeId) "
			+ "AND (:userId IS NULL OR d.updatedBy = :userId) AND (:packageId IS NULL OR d.packageId = :packageId)")
	List<DiagnosticServicePackageLine> findAllStoreAndPackageAndUser(String storeId, String userId, String packageId);
}
