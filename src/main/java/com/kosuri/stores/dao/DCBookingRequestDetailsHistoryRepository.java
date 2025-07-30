package com.kosuri.stores.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DCBookingRequestDetailsHistoryRepository extends JpaRepository<DCBookingRequestDetailsHistory, String> {

	List<DCBookingRequestDetailsHistory> findByServiceRequestId(String serviceRequestId);
	Optional<DCBookingRequestDetailsHistory> findByServiceRequestIdAndServiceRequestLineId(String ServiceRequestId, String serviceRequestLineId);
}
