package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreCashRegisterRepository extends JpaRepository<StoreCashRegisterEntity, Long> {

	List<StoreCashRegisterEntity> findByStoreId(String storeId);

	Optional<StoreCashRegisterEntity> findByStoreIdAndId(String storeId, Long storeCashId);
}
