package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DistributorRetailerOrderDetailsRepo extends JpaRepository<DistributorRetailerOrderDetailsEntity, String> {
	Optional<DistributorRetailerOrderDetailsEntity> findByOrderlineId(String orderLineId);
}
