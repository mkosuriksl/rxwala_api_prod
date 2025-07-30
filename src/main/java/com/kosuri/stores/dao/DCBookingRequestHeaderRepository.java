package com.kosuri.stores.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DCBookingRequestHeaderRepository extends JpaRepository<DCBookingRequestHeader, String> {

	List<DCBookingRequestHeader> findByUserIdStoreIdAndUpdatedDateBetweenAndAppointmentDateBetween(String userIdStoreId,
			LocalDate bookingDateFrom, LocalDate bookingDateTo, LocalDate appointmentDateFrom,
			LocalDate appointmentDateTo);

	List<DCBookingRequestHeader> findAll(Specification<DCBookingRequestHeader> specification);

	Optional<DCBookingRequestHeader> findByServiceRequestId(String serviceRequestId);
	
	Optional<List<DCBookingRequestHeader>> findByCustomerId(String ownerEmail);

}
