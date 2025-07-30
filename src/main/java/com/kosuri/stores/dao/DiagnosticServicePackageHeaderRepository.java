package com.kosuri.stores.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosticServicePackageHeaderRepository
		extends JpaRepository<DiagnosticServicePackageHeader, String> {

	@Query("SELECT d FROM DiagnosticServicePackageHeader d WHERE (:storeId IS NULL OR d.storeId = :storeId) "
			+ "AND (:userId IS NULL OR d.updatedBy = :userId) AND (:packageId IS NULL OR d.packageId = :packageId)")
	List<DiagnosticServicePackageHeader> findAllStoreAndUserAndPackage(String storeId, String userId, String packageId);
}
