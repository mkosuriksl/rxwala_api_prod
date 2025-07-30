package com.kosuri.stores.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DCPackageHeaderHistoryRepository extends JpaRepository<DCPackageHeaderHistory, String>{

	Optional<List<DCPackageHeaderHistory>> findByUserId(String cId);
}
