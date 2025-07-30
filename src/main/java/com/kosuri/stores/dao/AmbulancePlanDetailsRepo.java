package com.kosuri.stores.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AmbulancePlanDetailsRepo extends JpaRepository<AmbulancePlanDetails, Long> {

	AmbulancePlanDetails findByAmbulanceGenId(String ambulanceGenId);

}
