package com.kosuri.stores.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminDiagnosticServiceCategoryRepo extends JpaRepository<AdminDiagnosticServiceCategory, String> {

	AdminDiagnosticServiceCategory findByDcServiceCategoryId(String dcServiceCategoryId);

	@Query("SELECT DISTINCT adsc.dcServiceCategoryName FROM AdminDiagnosticServiceCategory adsc")
	List<String> findDcServiceCategoryName();

}
