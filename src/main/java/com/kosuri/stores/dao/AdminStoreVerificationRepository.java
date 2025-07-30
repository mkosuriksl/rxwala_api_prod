package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminStoreVerificationRepository extends JpaRepository<AdminStoreVerificationEntity, Integer> {

	Optional<AdminStoreVerificationEntity> findByUserIdStoreId(String userIdstoreId);
}
