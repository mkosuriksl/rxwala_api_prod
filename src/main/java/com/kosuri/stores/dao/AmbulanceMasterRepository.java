package com.kosuri.stores.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbulanceMasterRepository extends JpaRepository<AmbulanceMasterEntity, String> {

	@Query("SELECT a FROM AmbulanceMasterEntity a WHERE "
			+ "(:ambulanceRegNo IS NULL OR a.ambulanceRegNo = :ambulanceRegNo) AND "
			+ "(:userId IS NULL OR a.userId = :userId) AND "
			+ "(:phoneNumber IS NULL OR a.phoneNumber = :phoneNumber) AND "
			+ "(:baseLocation IS NULL OR a.baseLocation = :baseLocation) AND "
			+ "(:vehicleBrand IS NULL OR a.vehicleBrand = :vehicleBrand) AND "
			+ "(:vehicleModel IS NULL OR a.vehicleModel = :vehicleModel) AND "
			+ "(:rtoRegLocation IS NULL OR a.rtoRegLocation = :rtoRegLocation) AND "
			+ "(:state IS NULL OR a.state = :state) AND " + "(:vin IS NULL OR a.vin = :vin) AND "
			+ "(:ownerName IS NULL OR a.ownerName = :ownerName) AND " + "(:rtoDoc IS NULL OR a.rtoDoc = :rtoDoc) AND "
			+ "(:insuDoc IS NULL OR a.insuDoc = :insuDoc) AND "
			+ "(:ambLicDoc IS NULL OR a.ambLicDoc = :ambLicDoc) AND "
			+ "(:ventilator IS NULL OR a.ventilator = :ventilator) AND "
			+ "(:primaryCareNurse IS NULL OR a.primaryCareNurse = :primaryCareNurse) AND "
			+ "(:regDate IS NULL OR a.regDate = :regDate) AND "
		 + "(:image IS NULL OR a.image = :image) AND "
			+ "(:additionalFeatures IS NULL OR a.additionalFeatures = :additionalFeatures) AND "
			+ "(:verify IS NULL OR a.verify = :verify) AND " + "(:active IS NULL OR a.active = :active) AND "
			+ "(:verifiedBy IS NULL OR a.verifiedBy = :verifiedBy) AND "
			+ "(:updatedby IS NULL OR a.updatedby = :updatedby)")
	List<AmbulanceMasterEntity> searchAmbulances(@Param("ambulanceRegNo") String ambulanceRegNo,
			@Param("userId") String userId, @Param("phoneNumber") String phoneNumber,
			@Param("baseLocation") String baseLocation, @Param("vehicleBrand") String vehicleBrand,
			@Param("vehicleModel") String vehicleModel, @Param("rtoRegLocation") String rtoRegLocation,
			@Param("state") String state, @Param("vin") String vin, @Param("ownerName") String ownerName,
			@Param("rtoDoc") String rtoDoc, @Param("insuDoc") String insuDoc, @Param("ambLicDoc") String ambLicDoc,
			@Param("ventilator") Boolean ventilator, @Param("primaryCareNurse") String primaryCareNurse,
			@Param("regDate") LocalDateTime regDate,  @Param("image") String image,
			@Param("additionalFeatures") String additionalFeatures, @Param("verify") Boolean verify,
			@Param("active") Boolean active, @Param("verifiedBy") String verifiedBy,
			@Param("updatedby") String updatedby);

}
