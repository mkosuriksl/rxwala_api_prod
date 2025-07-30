package com.kosuri.stores.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbulanceBookingDetailRepository extends JpaRepository<AmbulanceBookingDetailEntity, String> {

	@Query("SELECT a FROM AmbulanceBookingDetailEntity a " + "WHERE (:bookingNo IS NULL OR a.bookingNo = :bookingNo) "
			+ "AND (:bookingDate IS NULL OR a.bookingDate = :bookingDate) "
			+ "AND (:patientName IS NULL OR a.patientName = :patientName) "
			+ "AND (:fromLocation IS NULL OR a.fromLocation = :fromLocation) "
			+ "AND (:toLocation IS NULL OR a.toLocation = :toLocation) "
			+ "AND (:customerContNum IS NULL OR a.customerContNum = :customerContNum) "
			+ "AND (:contactPerson IS NULL OR a.contactPerson = :contactPerson) "
			+ "AND (:bookedBy IS NULL OR a.bookedBy = :bookedBy) " + "AND (:status IS NULL OR a.status = :status) "
			+ "AND (:remarks IS NULL OR a.remarks = :remarks) " + "AND (:active IS NULL OR a.active = :active) "
			+ "AND (:createdOn IS NULL OR a.createdOn = :createdOn) "
			+ "AND (:createdBy IS NULL OR a.createdBy = :createdBy) "
			+ "AND (:updatedOn IS NULL OR a.updatedOn = :updatedOn) "
			+ "AND (:updatedBy IS NULL OR a.updatedBy = :updatedBy) "
			+ "AND (:ambulanceRegNo IS NULL OR a.ambulanceRegNo = :ambulanceRegNo)")
	List<AmbulanceBookingDetailEntity> searchAllFields(@Param("bookingNo") String bookingNo,
			@Param("bookingDate") LocalDateTime bookingDate, @Param("patientName") String patientName,
			@Param("fromLocation") String fromLocation, @Param("toLocation") String toLocation,
			@Param("customerContNum") String customerContNum, @Param("contactPerson") String contactPerson,
			@Param("bookedBy") String bookedBy, @Param("status") String status, @Param("remarks") String remarks,
			@Param("active") Boolean active, @Param("createdOn") LocalDateTime createdOn,
			@Param("createdBy") String createdBy, @Param("updatedOn") LocalDateTime updatedOn,
			@Param("updatedBy") String updatedBy, @Param("ambulanceRegNo") String ambulanceRegNo);

}
