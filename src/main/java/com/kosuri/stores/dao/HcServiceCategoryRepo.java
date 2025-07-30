package com.kosuri.stores.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HcServiceCategoryRepo extends JpaRepository<HcServiceCategory, String> {

	@Query("SELECT DISTINCT hcsc.serviceCategoryName from HcServiceCategory hcsc")
	List<String> findByHcDistinctServiceCategory();

}
