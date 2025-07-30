package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrimaryCareCenterRepository extends JpaRepository<PrimaryCareEntity, String> {
	List<PrimaryCareEntity> findByUserId(String userId);

	List<PrimaryCareEntity> findByStoreId(String storeId);

	List<PrimaryCareEntity> findByStoreIdAndUserId(String storeId, String userId);

//	Optional<PrimaryCareEntity> findByUserServiceIdAndUserId(String userServiceId, String userId);

//	Optional<PrimaryCareEntity> findByUserServiceId(String userServiceId);
	
	Optional<PrimaryCareEntity> findByUserIdStoreIdServiceId(String userServiceId);

	List<PrimaryCareEntity> findByUpdatedBy(String loggedInUserEmail);

	@Query("SELECT p FROM PrimaryCareEntity p WHERE  p.updatedBy = :userId "
			+ "AND (:storeId IS NULL OR p.storeId = :storeId) "
			+ "AND (:serviceId IS NULL OR p.serviceId = :serviceId)")
	List<PrimaryCareEntity> findByUpdatedByAndStoreIdAndServiceId(String userId, String storeId, String serviceId);

	List<PrimaryCareEntity> findAllByStoreIdIn(List<String> storeIds);

	List<PrimaryCareEntity> findByStoreIdInAndServiceCategory(List<String> storeIds, String serviceCategory);

	List<PrimaryCareEntity> findByStoreIdAndServiceId(String storeId, String serviceId);
	
	@Query("SELECT DISTINCT pc.serviceCategory FROM PrimaryCareEntity pc WHERE pc.serviceCategory IS NOT NULL")
	List<String> findPcServiceCategoryName();
}
