package com.kosuri.stores.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AmbulanceRepo extends JpaRepository<AddAmbulance, Long> {

	AddAmbulance findByAmbulanceRegNo(String ambulanceRegNo);

	AddAmbulance findByAmbulanceGenIdAndAmbulanceOwnerId(String ambulanceGenId, String ownerId);

	@Query("SELECT a FROM AddAmbulance a " +
		       "WHERE (:brand IS NULL OR a.ambulanceBrand = :brand) " +
		       "AND (:model IS NULL OR a.ambulanceModel = :model) " +
		       "AND (:startDate IS NULL OR a.ambulanceAddedDate >= :startDate) " +
		       "AND (:endDate IS NULL OR a.ambulanceAddedDate <= :endDate)")
		List<AddAmbulance> findByBrandOrModelOrStartDateBetweenAndEndDateBetween(
		       @Param("brand") String brand,
		       @Param("model") String model,
		       @Param("startDate") LocalDate startDate,
		       @Param("endDate") LocalDate endDate);

}
