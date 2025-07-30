package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminStoreCategoryRepo extends JpaRepository<AdminStoreCategory, String>{

	Optional<AdminStoreCategory> findByStoreCategoryId(String storeCategoryId);

}
