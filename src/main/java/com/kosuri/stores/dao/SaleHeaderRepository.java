package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleHeaderRepository extends JpaRepository<SaleHeaderEntity, String> {
	Optional<List<SaleHeaderEntity>> findByStoreId(String storeId);


}
