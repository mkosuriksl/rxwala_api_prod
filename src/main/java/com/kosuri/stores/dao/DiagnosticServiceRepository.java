package com.kosuri.stores.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosticServiceRepository extends JpaRepository<DiagnosticServicesEntity, String> {
	List<DiagnosticServicesEntity> findByUserId(String userId);

	List<DiagnosticServicesEntity> findByStoreId(String storeId);

	List<DiagnosticServicesEntity> findByStoreIdAndUserId(String storeId, String userId);

	@Query("SELECT d FROM DiagnosticServicesEntity d WHERE (:storeId IS NULL OR d.storeId = :storeId) "
			+ "AND (:serviceId IS NULL OR d.serviceId = :serviceId)")
	List<DiagnosticServicesEntity> findAllByStoreIdAndServiceId(String storeId, String serviceId);

	List<DiagnosticServicesEntity> findAllByStoreIdIn(List<String> storeIds);
	
	List<DiagnosticServicesEntity> findByServiceCategory(String type);

	List<DiagnosticServicesEntity> findByStoreIdInAndServiceCategory(List<String> storeIds, String serviceCategory);

	List<DiagnosticServicesEntity> findByStoreIdIn(List<String> storeIds);

}
