package com.kosuri.stores.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerMembershipRequestRepo extends JpaRepository<CustomerMembershipRequest, Long> {

	Optional<CustomerMembershipRequest> findByCustomerOrderId(String customerOrderId);

	Optional<CustomerMembershipRequest> findByOrderId(String orderId);

}
