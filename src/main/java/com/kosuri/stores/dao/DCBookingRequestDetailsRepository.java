package com.kosuri.stores.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosuri.stores.model.enums.Status;

@Repository
public interface DCBookingRequestDetailsRepository extends JpaRepository<DCBookingRequestDetails, String> {

	List<DCBookingRequestDetails> findByServiceRequestId(String serviceRequestId);
	Optional<DCBookingRequestDetails> findByServiceRequestIdAndServiceRequestLineId(String serviceRequestId, String serviceRequestLineId);
	List<DCBookingRequestDetails> findByServiceRequestIdAndStatusNot(String serviceRequestId, Status canceled);
}
