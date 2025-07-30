package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRequestRepo extends JpaRepository<MembershipRequest, Long> {

	Optional<MembershipRequest> findByPgOrderId(String pgOrderId);

	Optional<MembershipRequest> findByOrderId(String orderId);

}
