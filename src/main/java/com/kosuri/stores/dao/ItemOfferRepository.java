package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOfferRepository extends JpaRepository<ItemOfferEntity, String> {

	Optional<ItemOfferEntity> findByUserIdStoreIdItemCode(String userIdStoreIdItemCode);

	ItemOfferEntity findByUserIdStoreIdItemCodeAndBatchNumber(String userIdStoreIdItemCode, String batch);
}
