package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderDetailsRepo extends JpaRepository<CustomerOrderDetailsEntity, String> {
	Optional<CustomerOrderDetailsEntity> findByOrderlineId(String orderLineId);
}
