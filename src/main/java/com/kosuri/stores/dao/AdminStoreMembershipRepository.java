package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminStoreMembershipRepository extends JpaRepository<AdminStoreMembershipEntity, String> {

	Optional<AdminStoreMembershipEntity> findByStoreCategory(String storeCategory);

	Optional<AdminStoreMembershipEntity> findByPlanIdAndStoreCategory(String planid, String storeCategory);

	Optional<AdminStoreMembershipEntity> findByPlanIdStoreCategory(String planIdStoreCategory);

	List<AdminStoreMembershipEntity> findByPlanIdNot(String planId);
}
