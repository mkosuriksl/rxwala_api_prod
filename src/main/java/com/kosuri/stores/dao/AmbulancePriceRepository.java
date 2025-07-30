package com.kosuri.stores.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbulancePriceRepository extends JpaRepository<AmbulancePriceEntity, String> {

	@Query("SELECT a FROM AmbulancePriceEntity a WHERE " + "(:priceId IS NULL OR a.priceId = :priceId) AND "
			+ "(:ambulanceRegNo IS NULL OR a.ambulanceRegNo = :ambulanceRegNo) AND "
			+ "(:pricePerKm IS NULL OR a.pricePerKm = :pricePerKm) AND "
			+ "(:updateSrcLocation IS NULL OR a.updateSrcLocation = :updateSrcLocation) AND "
			+ "(:updateDate IS NULL OR a.updateDate = :updateDate) AND "
			+ "(:waitingCharges IS NULL OR a.waitingCharges = :waitingCharges) AND "
			+ "(:driverLicNo IS NULL OR a.driverLicNo = :driverLicNo) AND "
			+ "(:driverName IS NULL OR a.driverName = :driverName) AND "
			+ "(:active IS NULL OR a.active = :active) AND " + "(:createdOn IS NULL OR a.createdOn = :createdOn) AND "
			+ "(:createdBy IS NULL OR a.createdBy = :createdBy) AND "
			+ "(:updatedOn IS NULL OR a.updatedOn = :updatedOn) AND "
			+ "(:updatedBy IS NULL OR a.updatedBy = :updatedBy) AND "
			+ "(:contactNumber IS NULL OR a.contactNumber = :contactNumber)")
	List<AmbulancePriceEntity> searchAmbulancePrices(@Param("priceId") Long priceId,
			@Param("ambulanceRegNo") String ambulanceRegNo, @Param("pricePerKm") Double pricePerKm,
			@Param("updateSrcLocation") String updateSrcLocation, @Param("updateDate") LocalDateTime updateDate,
			@Param("waitingCharges") Double waitingCharges, @Param("driverLicNo") String driverLicNo,
			@Param("driverName") String driverName, @Param("active") Boolean active,
			@Param("createdOn") LocalDateTime createdOn, @Param("createdBy") String createdBy,
			@Param("updatedOn") LocalDateTime updatedOn, @Param("updatedBy") String updatedBy,
			@Param("contactNumber") String contactNumber);

}
