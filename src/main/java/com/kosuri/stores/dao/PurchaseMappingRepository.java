package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseMappingRepository extends JpaRepository<PurchaseMappingEntity, Long> {
    boolean existsByUserIdStoreIdItemCode(String userIdStoreIdItemCode);
}