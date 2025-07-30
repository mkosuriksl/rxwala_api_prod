package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminStoreBusinessTypeRepository extends JpaRepository<AdminStoreBusinessTypeEntity, Integer> {
	Optional<AdminStoreBusinessTypeEntity> findByBusinessName(String businessType);
	boolean existsByBusinessTypeId(String businessTypeId);
}
