package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseHeaderRepository extends JpaRepository<PurchaseHeaderEntity, String> {

	List<PurchaseHeaderEntity> findAll(Specification<PurchaseHeaderEntity> spec);
	
	Optional<List<PurchaseHeaderEntity>> findByStoreId(String storeId);

	Page<PurchaseHeaderEntity> findAll(Specification<PurchaseHeaderEntity> spec, Pageable pageable);
	

}
