package com.kosuri.stores.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DCPackageDetailsRepository extends JpaRepository<DCPackageDetails, String> {

	List<DCPackageDetails> findByPackageId(String packageId);
	Optional<DCPackageDetails> findByPackageIdAndPackageIdLineId(String packageId, String packageIdLineId);
}
