package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DcBookingHeaderRepository extends JpaRepository<DcBookingHeader , String>{

	Optional<DcBookingHeader> findByServiceRequestId(String serviceRequestId);

	List<DcBookingHeader> findAll(Specification<DcBookingHeader> specification);

}
