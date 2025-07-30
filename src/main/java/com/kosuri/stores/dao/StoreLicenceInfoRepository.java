package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreLicenceInfoRepository extends JpaRepository<StoreLicenceInfoEntity, String> {

	StoreLicenceInfoEntity findByStoreId(String storeId);
}
