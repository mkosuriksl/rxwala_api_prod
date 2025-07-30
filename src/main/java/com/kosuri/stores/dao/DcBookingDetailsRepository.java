package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosuri.stores.model.enums.Status;

@Repository
public interface DcBookingDetailsRepository extends JpaRepository<DcBookingDetails, String>{

	List<DcBookingDetails> findByServiceRequestId(String serviceRequestId);
	
	Optional<DcBookingDetails> findByServiceRequestIdAndServiceRequestLineId(
	        String serviceRequestId, String serviceRequestIdLineId);

	List<DcBookingDetails> findByServiceRequestIdAndStatusNot(String serviceRequestId, Status canceled);

	long countByServiceRequestIdAndStatusNot(String serviceRequestId, Status canceled);

}
