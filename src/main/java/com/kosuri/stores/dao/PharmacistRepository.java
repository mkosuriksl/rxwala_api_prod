package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacistRepository extends JpaRepository<PharmacistEntity, String> {
	
	Optional<PharmacistEntity> findByPharmacistEmailAddressOrPharmacistContact(String pharmaUserEmail,
			String pharmaUserContact);
	
	Optional<PharmacistEntity> findByPharmacistEmailAddress(String pharmaUserEmail);

	List<PharmacistEntity> findByPharmacistEmailAddressOrPharmacistContactOrPharmacistAvailableLocation(
			String emailAddress, String mobileNumber, String availableLocation);

	@Query("SELECT p FROM PharmacistEntity p WHERE "
			+ "(:emailAddress IS NULL OR p.pharmacistEmailAddress = :emailAddress) "
			+ "OR (:mobileNumber IS NULL OR p.pharmacistContact = :mobileNumber) "
			+ "OR (:availableLocation IS NOT NULL AND p.pharmacistAvailableLocation = :availableLocation) "
			+ "OR (:pharmacistId IS NULL OR p.pharmacistId = :pharmacistId)")
	List<PharmacistEntity> findAllByEmailContactOrAvailableLocationAndPharmacistId(
			@Param("emailAddress") String emailAddress, @Param("mobileNumber") String mobileNumber,
			@Param("availableLocation") String availableLocation, @Param("pharmacistId") String pharmacistId);

}
